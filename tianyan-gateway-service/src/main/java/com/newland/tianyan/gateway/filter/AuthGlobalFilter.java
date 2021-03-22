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
 * 授权解析token信息并统一填充header中
 *
 * @Author: huangJunJie  2021-03-04 16:25
 */
public class AuthGlobalFilter implements GlobalFilter, Ordered {
    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        String token = exchange.getRequest().getHeaders().getFirst("Authorization");
        if (token == null || token.isEmpty()) {
            return chain.filter(exchange);
        }
        String clientCredential = "client_credentials";
        String passwordCredential = "password";
        String headAppId = "app_id";
        String tokenAppId1 = "app_id";
        String tokenAppId2 = "appId";

        String headAccount = "account";
        String tokenAccount1 = "account";
        String tokenAccount2 = "user_name";
        try {
            String realToken = token.replace("Bearer ", "");
            JWSObject jwsObject = JWSObject.parse(realToken);
            String info = jwsObject.getPayload().toString();
            JSONObject jsonObject = JSON.parseObject(info);
            String grantType = jsonObject.getString("grant_type");
            if (clientCredential.equals(grantType)) {
                String appId = "";
                if (jsonObject.containsKey(tokenAppId1)) {
                    appId = jsonObject.getString(tokenAppId1);
                } else if (jsonObject.containsKey(tokenAppId2)) {
                    appId = jsonObject.getString(tokenAppId2);
                }
                ServerHttpRequest request = exchange.getRequest().mutate().header(headAppId, appId)
                        .header(headAccount, jsonObject.getString(tokenAccount1))
                        .build();
                exchange = exchange.mutate().request(request).build();
            } else if (passwordCredential.equals(grantType)) {
                ServerHttpRequest request = exchange.getRequest().mutate().header(headAccount, jsonObject.getString(tokenAccount2)).build();
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
