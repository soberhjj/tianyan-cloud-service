package com.newland.tianyan.gateway.filter;

import com.newland.tianya.commons.base.support.JsonSkipSupport;
import com.newland.tianyan.gateway.support.GatewayLoggerSupport;
import com.newland.tianyan.gateway.support.ResponseBodyTraceIdDecorator;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.reactivestreams.Publisher;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.cloud.gateway.filter.NettyWriteResponseFilter;
import org.springframework.core.Ordered;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.core.io.buffer.DataBufferFactory;
import org.springframework.core.io.buffer.DataBufferUtils;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.http.server.reactive.ServerHttpResponseDecorator;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;

/**
 * 会话响应信息打印
 *
 * @author: RojiaHuang
 * @date: 2021/2/27
 */
@Component
@Slf4j
public class ApiLogRespFilter extends GatewayLoggerSupport implements GlobalFilter, Ordered {

    @SneakyThrows
    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        ServerHttpResponse serverHttpResponse = exchange.getResponse();
        DataBufferFactory dataBufferFactory = serverHttpResponse.bufferFactory();
        //后置过滤器，发生在ApiLogReqFilter后置过滤器顺序之后
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
                        String traceId = getTradeIdFromHeads(serverHttpResponse);
                        String makeUpTraceIdRequestBody = ResponseBodyTraceIdDecorator.putTraceId(responseBody,traceId);

                        log.info("requestTime:{},responseTime:{},responseBody:{}", getRequestTimeFromHeads(serverHttpResponse), LocalDateTime.now().toString(), makeUpTraceIdRequestBody);
                        byte[] uppedContent = makeUpTraceIdRequestBody.getBytes(StandardCharsets.UTF_8);
                        return dataBufferFactory.wrap(uppedContent);
                    }));
                }
                return super.writeWith(body);
            }
        };
        return chain.filter(exchange.mutate().response(decoratedResponse).build());
    }


    @Override
    public int getOrder() {
        return NettyWriteResponseFilter.WRITE_RESPONSE_FILTER_ORDER - 1;
    }
}
