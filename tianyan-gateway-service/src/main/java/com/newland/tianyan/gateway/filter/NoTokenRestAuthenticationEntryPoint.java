package com.newland.tianyan.gateway.filter;

import com.newland.tianyan.gateway.constant.GatewayErrorEnums;
import org.slf4j.MDC;
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
 * 自定义返回结果：没有token
 * @author Administrator
 */

@Component
public class NoTokenRestAuthenticationEntryPoint implements ServerAuthenticationEntryPoint {
    @Override
    public Mono<Void> commence(ServerWebExchange exchange, AuthenticationException e) {
        ServerHttpResponse response = exchange.getResponse();
        response.setStatusCode(HttpStatus.OK);
        response.getHeaders().add(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE);
        GatewayErrorEnums errorEnums = GatewayErrorEnums.NO_TOKEN;
        String body = "{\"trace_id\":" + MDC.get("traceId") + ",\"error_code\":" + errorEnums.getErrorCode() + ",\"error_msg\": \"" + errorEnums.getErrorMsg() + "\"}";
//        String body="{\"code\":6100,\"message\":No Access token}";
        DataBuffer buffer = response.bufferFactory().wrap(body.getBytes(Charset.forName("UTF-8")));
        return response.writeWith(Mono.just(buffer));
    }
}