package com.newland.tianyan.gateway.filter;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.nimbusds.jose.JWSObject;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.text.ParseException;


/**
 * @Author: huangJunJie  2021-03-04 16:25
 */
public class AuthGlobalFilter implements GlobalFilter, Ordered {
    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        String token = exchange.getRequest().getHeaders().getFirst("Authorization");
        if (token == null || token.isEmpty()) {
            return chain.filter(exchange);
        }
        try {
            String realToken = token.replace("Bearer ", "");
            JWSObject jwsObject = JWSObject.parse(realToken);
            String info = jwsObject.getPayload().toString();
            JSONObject jsonObject = JSON.parseObject(info);
            String grantType = jsonObject.getString("grant_type");
            if ("client_credentials".equals(grantType)) {
                String appId = "";
                if (jsonObject.containsKey("app_id")) {
                    appId = jsonObject.getString("app_id");
                } else if (jsonObject.containsKey("appId")) {
                    appId = jsonObject.getString("appId");
                }
                ServerHttpRequest request = exchange.getRequest().mutate().header("app_id", appId)
                        .header("account", jsonObject.getString("account"))
                        .build();
                exchange = exchange.mutate().request(request).build();
            } else if ("password".equals(grantType)) {
                ServerHttpRequest request = exchange.getRequest().mutate().header("account", jsonObject.getString("user_name")).build();
                exchange = exchange.mutate().request(request).build();
            }
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return chain.filter(exchange);
    }

    @Override
    public int getOrder() {
        return -2000;
    }
}
