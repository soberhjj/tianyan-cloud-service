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

import static com.newland.tianya.commons.base.constants.TokenConstants.*;


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
        try {
            String realToken = token.replace("Bearer ", "");
            JWSObject jwsObject = JWSObject.parse(realToken);
            String info = jwsObject.getPayload().toString();
            JSONObject jsonObject = JSON.parseObject(info);
            String grantType = jsonObject.getString("grant_type");
            if (CLIENT_CREDENTIAL.equals(grantType)) {
                String appId = "";
                if (jsonObject.containsKey(TOKEN_APP_ID_1)) {
                    appId = jsonObject.getString(TOKEN_APP_ID_1);
                } else if (jsonObject.containsKey(TOKEN_APP_ID_2)) {
                    appId = jsonObject.getString(TOKEN_APP_ID_2);
                }
                ServerHttpRequest request = exchange.getRequest().mutate()
                        .header(HEAD_APP_ID, appId)
                        .header(HEAD_ACCOUNT, jsonObject.getString(TOKEN_ACCOUNT_1))
                        .build();
                exchange = exchange.mutate().request(request).build();
            } else if (PASSWORD_CREDENTIAL.equals(grantType)) {
                ServerHttpRequest request = exchange.getRequest().mutate().header(HEAD_ACCOUNT, jsonObject.getString(TOKEN_ACCOUNT_2)).build();
                exchange = exchange.mutate().request(request).build();
            }
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return chain.filter(exchange);
    }

    @Override
    public int getOrder() {
        return -3000;
    }
}
