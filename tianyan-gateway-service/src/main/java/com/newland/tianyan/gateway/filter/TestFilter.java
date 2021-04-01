//package com.newland.tianyan.gateway.filter;
//
//import com.newland.tianyan.gateway.support.GatewayLoggerSupport;
//import org.springframework.cloud.gateway.filter.GatewayFilterChain;
//import org.springframework.cloud.gateway.filter.GlobalFilter;
//import org.springframework.core.Ordered;
//import org.springframework.http.server.reactive.ServerHttpRequest;
//import org.springframework.stereotype.Component;
//import org.springframework.web.server.ServerWebExchange;
//import reactor.core.publisher.Mono;
//
///**
// * @author: RojiaHuang
// * @description:
// * @date: 2021/3/22
// */
//@Component
//public class TestFilter extends GatewayLoggerSupport implements GlobalFilter, Ordered {
//    @Override
//    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
//        ServerHttpRequest serverHttpRequest = exchange.getRequest();
////        System.out.println("Test!!!!!:HEAD_REQUEST_TIME" + serverHttpRequest.getHeaders().containsKey(HEAD_REQUEST_TIME));
////        System.out.println("Test!!!!!:HEAD_REQUEST_TIME" + serverHttpRequest.getHeaders().getFirst(HEAD_REQUEST_TIME));
////        System.out.println("Test!!!!!:GATEWAY_TRACE_HEAD：" + getTradeIdFromHeads(exchange.getRequest()));
//        return chain.filter(exchange);
//    }
//
//    @Override
//    public int getOrder() {
//        return 10100;
//    }
//}
