package com.newland.tianyan.gateway.filter;

import com.newland.tianyan.gateway.utils.LogFixColumnsUtils;
import com.newland.tianyan.gateway.utils.ReactiveAddrUtils;
import lombok.SneakyThrows;
import org.apache.skywalking.apm.toolkit.trace.TraceContext;
import org.slf4j.MDC;
import org.springframework.cloud.gateway.filter.GatewayFilter;
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

    @SneakyThrows
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
        //统一http
        ServerWebExchange build = exchange.mutate().request(serverHttpRequest).build();
        return chain.filter(build);
    }

    @Override
    public int getOrder() {
        return Ordered.HIGHEST_PRECEDENCE;
    }
}
