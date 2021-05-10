package com.newland.tianyan.gateway.filter;

import lombok.SneakyThrows;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.stereotype.Component;
import org.springframework.util.MultiValueMap;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebFilter;
import org.springframework.web.server.WebFilterChain;
import reactor.core.publisher.Mono;

import java.net.URI;

/**
 * @Author: huangJunJie  2021-05-08 15:20
 */
@Component
public class AccessTokenFilter implements WebFilter {
    @SneakyThrows
    @Override
    public Mono<Void> filter(ServerWebExchange exchange, WebFilterChain chain) {
        MultiValueMap<String, String> queryParams = exchange.getRequest().getQueryParams();
        if (queryParams.get("access_token") != null) {
            String token = "Bearer " + queryParams.get("access_token").get(0);
            String uri = exchange.getRequest().getURI().toString().split("\\?access_token")[0];
            ServerHttpRequest request = exchange.getRequest().mutate().header("Authorization", token).build().mutate().uri(new URI(uri)).build();
            exchange = exchange.mutate().request(request).build();
            return chain.filter(exchange);
        }
        return chain.filter(exchange);
    }
}
