package com.newland.tianyan.gateway.filter;

import com.newland.tianyan.gateway.utils.LogFixColumnsUtils;
import com.newland.tianyan.gateway.utils.ReactiveAddrUtils;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.apache.skywalking.apm.toolkit.trace.TraceContext;
import org.slf4j.MDC;
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
public class ApiLogReqFilter implements GlobalFilter, Ordered {

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        //链路生成追踪id
        String traceId = TraceContext.traceId();
        //投放至日志
        MDC.put(TRACE_MDC, traceId);
        //投放至httpHead
        ServerHttpRequest serverHttpRequest = exchange.getRequest().mutate()
                .headers(header -> {
                    header.add(GATEWAY_TRACE_HEAD, traceId);
                })
                .build();
        String url = serverHttpRequest.getURI().getPath();
        String clientIp = ReactiveAddrUtils.getRemoteAddr(serverHttpRequest);
        String serverIp = ReactiveAddrUtils.getLocalAddr();
        //日志固定列
        LogFixColumnsUtils.init(url, clientIp, serverIp);
        //ServerHttpRequest serverHttpRequest = exchange.getRequest();
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
