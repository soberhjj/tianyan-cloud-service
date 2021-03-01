package com.newland.tianyan.gateway.log;

import org.apache.skywalking.apm.toolkit.trace.TraceContext;
import org.slf4j.MDC;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import static com.newland.tianyan.gateway.constant.GlobalTraceConstant.*;

/**
 * @author: RojiaHuang
 * @description:
 * @date: 2021/2/27
 */
@Component
public class TraceFilter implements GlobalFilter, Ordered {

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
                    header.add(SPAN_HEAD, "0");
                })
                .build();
        //统一http
        ServerWebExchange build = exchange.mutate().request(serverHttpRequest).build();
        return chain.filter(build);
    }

    @Override
    public int getOrder() {
        return Ordered.HIGHEST_PRECEDENCE;
    }
}
