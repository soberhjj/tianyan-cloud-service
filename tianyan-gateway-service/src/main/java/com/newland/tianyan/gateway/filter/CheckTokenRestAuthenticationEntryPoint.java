package com.newland.tianyan.gateway.filter;

import com.newland.tianyan.common.constans.GlobalArgumentErrorEnums;
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
 * 自定义返回结果：token校验失败（即token是无效的）、token过期
 *
 * @author Administrator
 */

@Component
public class CheckTokenRestAuthenticationEntryPoint implements ServerAuthenticationEntryPoint {
    @Override
    public Mono<Void> commence(ServerWebExchange exchange, AuthenticationException e) {
        ServerHttpResponse response = exchange.getResponse();
        response.setStatusCode(HttpStatus.OK);
        response.getHeaders().add(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE);
        String message = e.getMessage();
        if (message.startsWith("Jwt expired")) {
            GlobalArgumentErrorEnums errorEnums = GlobalArgumentErrorEnums.TOKEN_EXPIRED;
            String body = "{\"trace_id\":" + MDC.get("traceId") + ",\"error_code\":" + errorEnums.getErrorCode() + ",\"error_msg\": \"" + errorEnums.getErrorMsg() + "\"}";
            DataBuffer buffer = response.bufferFactory().wrap(body.getBytes(Charset.forName("UTF-8")));
            return response.writeWith(Mono.just(buffer));
        } else {
            GlobalArgumentErrorEnums errorEnums = GlobalArgumentErrorEnums.TOKEN_INVALID;
            String body = "{\"trace_id\":" + MDC.get("traceId") + ",\"error_code\":" + errorEnums.getErrorCode() + ",\"error_msg\": \"" + errorEnums.getErrorMsg() + "\"}";
            DataBuffer buffer = response.bufferFactory().wrap(body.getBytes(Charset.forName("UTF-8")));
            return response.writeWith(Mono.just(buffer));
        }
    }
}