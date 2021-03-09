package com.newland.tianyan.gateway.filter;

import com.newland.tianyan.gateway.utils.LogFixColumnsUtils;
import com.newland.tianyan.gateway.utils.ReactiveAddrUtils;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.reactivestreams.Publisher;
import org.slf4j.MDC;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.cloud.gateway.filter.ForwardRoutingFilter;
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
import org.springframework.web.reactive.DispatcherHandler;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;

import static com.newland.tianyan.gateway.constant.GlobalTraceConstant.GATEWAY_TRACE_HEAD;
import static com.newland.tianyan.gateway.constant.GlobalTraceConstant.TRACE_MDC;

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
        //日志固定列
        String url = serverHttpRequest.getURI().getPath();
        String clientIp = ReactiveAddrUtils.getRemoteAddr(serverHttpRequest);
        String serverIp = ReactiveAddrUtils.getLocalAddr();
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
                if (getHeaders().containsKey(GATEWAY_TRACE_HEAD)) {
                    String traceId = getHeaders().get(GATEWAY_TRACE_HEAD).get(0);
                    //投放至日志
                    MDC.put(TRACE_MDC, traceId);
                }
                if (body instanceof Flux) {
                    Flux<? extends DataBuffer> fluxBody = (Flux<? extends DataBuffer>) body;
                    return super.writeWith(fluxBody.map(dataBuffer -> {
                        byte[] content = new byte[dataBuffer.readableByteCount()];
                        dataBuffer.read(content);

                        DataBufferUtils.release(dataBuffer);
                        String responseBody = new String(content, StandardCharsets.UTF_8);
                        log.info("requestTime:{},responseTime:{},responseBody:{}", requestTime, responseTime, responseBody);
                        byte[] uppedContent = new String(content, StandardCharsets.UTF_8).getBytes();
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
        return -3;
    }
}
