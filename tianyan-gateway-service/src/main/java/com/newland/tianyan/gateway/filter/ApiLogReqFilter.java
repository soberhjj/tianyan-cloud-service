package com.newland.tianyan.gateway.filter;

import com.newland.tianyan.gateway.utils.LogFixColumnsUtils;
import com.newland.tianyan.gateway.utils.ReactiveAddrUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.skywalking.apm.toolkit.trace.TraceContext;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.cloud.gateway.filter.ForwardRoutingFilter;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.core.io.buffer.DataBufferUtils;
import org.springframework.http.HttpMethod;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpRequestDecorator;
import org.springframework.stereotype.Component;
import org.springframework.util.MultiValueMap;
import org.springframework.web.reactive.DispatcherHandler;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;

/**
 * @author: RojiaHuang
 * @description: 会话请求打印，生成日志链路追踪TraceId并传入header中
 * @date: 2021/2/27
 */
@Component
@Slf4j
public class ApiLogReqFilter implements GlobalFilter, GatewayFilter, Ordered {

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        String traceId = TraceContext.traceId();
        System.out.println("traceID：" + traceId);

        ServerHttpRequest serverHttpRequest = exchange.getRequest();
        String url = serverHttpRequest.getURI().getPath();
        String clientIp = ReactiveAddrUtils.getRemoteAddr(serverHttpRequest);
        String serverIp = ReactiveAddrUtils.getLocalAddr();
        //日志固定列
        LogFixColumnsUtils.init(url, clientIp, serverIp);
        //请求时间
        String requestTime = LocalDateTime.now().toString();
        exchange.getAttributes().put("requestTime", requestTime);
        //请求参数
        String method = serverHttpRequest.getMethodValue();
        if (HttpMethod.POST.matches(method)) {
            return DataBufferUtils.join(exchange.getRequest().getBody())
                    .flatMap(dataBuffer -> {
                        byte[] bytes = new byte[dataBuffer.readableByteCount()];
                        dataBuffer.read(bytes);
                        String requestBody = new String(bytes, StandardCharsets.UTF_8);
                        log.info("requestTime:{},requestParams:{}", requestTime, requestBody);

                        exchange.getAttributes().put("POST_BODY", requestBody);
                        DataBufferUtils.release(dataBuffer);
                        Flux<DataBuffer> cachedFlux = Flux.defer(() -> {
                            DataBuffer buffer = exchange.getResponse().bufferFactory()
                                    .wrap(bytes);
                            return Mono.just(buffer);
                        });

                        ServerHttpRequest mutatedRequest = new ServerHttpRequestDecorator(
                                exchange.getRequest()) {
                            @Override
                            public Flux<DataBuffer> getBody() {
                                return cachedFlux;
                            }
                        };
                        return chain.filter(exchange.mutate().request(mutatedRequest)
                                .build());
                    });
        } else if (HttpMethod.GET.matches(method)) {
            MultiValueMap<String, String> requestParams = serverHttpRequest.getQueryParams();
            log.info("requestTime:{},requestParams:{}", requestTime, requestParams.toString());
        }
        return chain.filter(exchange);
    }

    @Override
    public int getOrder() {
        return -5;
    }
}
