package com.newland.tianyan.gateway.config;

import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.server.ServerAuthenticationEntryPoint;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.nio.charset.Charset;

/**
 * 自定义返回结果：token校验失败（即token是无效的）、token过期
 */

@Component
public class CheckTokenRestAuthenticationEntryPoint implements ServerAuthenticationEntryPoint {
    @Override
    public Mono<Void> commence(ServerWebExchange exchange, AuthenticationException e) {
        ServerHttpResponse response = exchange.getResponse();
        response.setStatusCode(HttpStatus.OK);
        response.getHeaders().add(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE);
        String message = e.getMessage();
        if (message.startsWith("Jwt expired")){
            String body = "{\"code\":6102,\"message\":Access token expired}";
            DataBuffer buffer = response.bufferFactory().wrap(body.getBytes(Charset.forName("UTF-8")));
            return response.writeWith(Mono.just(buffer));
        }else {
            String body = "{\"code\":6101,\"message\":Access token invalid}";
            DataBuffer buffer = response.bufferFactory().wrap(body.getBytes(Charset.forName("UTF-8")));
            return response.writeWith(Mono.just(buffer));
        }
    }
}