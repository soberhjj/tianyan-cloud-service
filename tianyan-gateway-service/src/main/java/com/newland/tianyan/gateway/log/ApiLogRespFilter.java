package com.newland.tianyan.gateway.log;

import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.reactivestreams.Publisher;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.core.io.buffer.DataBufferFactory;
import org.springframework.core.io.buffer.DataBufferUtils;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.http.server.reactive.ServerHttpResponseDecorator;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;

/**
 * @author: RojiaHuang
 * @description: 会话请求打印，生成日志链路追踪TraceId并传入header中
 * @date: 2021/2/27
 */
@Component
@Slf4j
public class ApiLogRespFilter implements GlobalFilter, Ordered {

    @SneakyThrows
    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        ServerHttpRequest serverHttpRequest = exchange.getRequest();
        String url = serverHttpRequest.getURI().getPath();
        String clientIp = ReactiveAddrUtils.getRemoteAddr(serverHttpRequest);
        String serverIp = ReactiveAddrUtils.getLocalAddr();
        //日志固定列
        LogFixColumnsUtils.init(url, clientIp, serverIp);
        //响应时间
        String requestTime = exchange.getAttribute("requestTime");
        String responseTime = LocalDateTime.now().toString();
        //响应参数
        ServerHttpResponse serverHttpResponse = exchange.getResponse();
        DataBufferFactory dataBufferFactory = serverHttpResponse.bufferFactory();
        ServerHttpResponseDecorator decoratedResponse = new ServerHttpResponseDecorator(serverHttpResponse) {
            @Override
            public Mono<Void> writeWith(Publisher<? extends DataBuffer> body) {
                if (body instanceof Flux) {
                    Flux<? extends DataBuffer> fluxBody = (Flux<? extends DataBuffer>) body;
                    return super.writeWith(fluxBody.map(dataBuffer -> {
                        byte[] content = new byte[dataBuffer.readableByteCount()];
                        dataBuffer.read(content);

                        DataBufferUtils.release(dataBuffer);
                        String responseBody = new String(content, StandardCharsets.UTF_8);
                        log.info("requestTime:{},responseTime:{},responseBody:{}", requestTime, responseTime,responseBody);
                        byte[] uppedContent = new String(content, StandardCharsets.UTF_8).getBytes();
                        return dataBufferFactory.wrap(uppedContent);
                    }));
                }
                return super.writeWith(body);
            }
        };
        LogFixColumnsUtils.clear();
        return chain.filter(exchange.mutate().response(decoratedResponse).build());
    }

    @Override
    public int getOrder() {
        return -3;
    }
}
