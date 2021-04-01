//package com.newland.tianyan.gateway.filter;
//
//import com.newland.tianya.commons.base.generator.IDUtil;
//import com.newland.tianya.commons.base.utils.LogFixColumnUtils;
//import com.newland.tianya.commons.base.utils.LogIdUtils;
//import com.newland.tianyan.gateway.support.ReactiveAddrUtils;
//import lombok.extern.slf4j.Slf4j;
//import org.slf4j.MDC;
//import org.springframework.cloud.gateway.filter.GatewayFilterChain;
//import org.springframework.cloud.gateway.filter.GlobalFilter;
//import org.springframework.core.Ordered;
//import org.springframework.core.io.buffer.DataBuffer;
//import org.springframework.core.io.buffer.DataBufferUtils;
//import org.springframework.http.HttpMethod;
//import org.springframework.http.server.reactive.ServerHttpRequest;
//import org.springframework.http.server.reactive.ServerHttpRequestDecorator;
//import org.springframework.stereotype.Component;
//import org.springframework.util.MultiValueMap;
//import org.springframework.web.server.ServerWebExchange;
//import reactor.core.publisher.Flux;
//import reactor.core.publisher.Mono;
//
//import java.nio.charset.StandardCharsets;
//import java.time.LocalDateTime;
//
//import static com.newland.tianya.commons.base.constants.GlobalLogConstant.GATEWAY_TRACE_HEAD;
//import static com.newland.tianya.commons.base.constants.GlobalLogConstant.TRACE_MDC;
//import static org.springframework.cloud.gateway.filter.RouteToRequestUrlFilter.ROUTE_TO_URL_FILTER_ORDER;
//
///**
// * @author: RojiaHuang
// * @description:
// * @date: 2021/3/27
// */
//@Component
//@Slf4j
//public class TestReqFilter2 implements GlobalFilter, Ordered {
//
//    @Override
//    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
//        //链路生成追踪id
//        String traceId = LogIdUtils.traceId();
//        System.out.println("TestReqFilter2-traceId：" + traceId);
//        //投放至日志
//        MDC.put(TRACE_MDC, traceId);
//        //投放至httpHead
//        ServerHttpRequest serverHttpRequest = exchange.getRequest().mutate()
//                .headers(header -> {
//                    header.add(GATEWAY_TRACE_HEAD, traceId);
//                })
//                .build();
//        String url = serverHttpRequest.getURI().getPath();
//        String clientIp = ReactiveAddrUtils.getRemoteAddr(serverHttpRequest);
//        String serverIp = ReactiveAddrUtils.getLocalAddr();
//        //日志固定列
//        LogFixColumnUtils.init(url, clientIp, serverIp);
//        //ServerHttpRequest serverHttpRequest = exchange.getRequest();
//        //请求时间
//        String requestTime = LocalDateTime.now().toString();
//        exchange.getAttributes().put("requestTime", requestTime);
//        //请求参数
//        String method = serverHttpRequest.getMethodValue();
//        if (HttpMethod.POST.matches(method)) {
//            return DataBufferUtils.join(exchange.getRequest().getBody())
//                    .flatMap(dataBuffer -> {
//                        byte[] bytes = new byte[dataBuffer.readableByteCount()];
//                        dataBuffer.read(bytes);
//                        String requestBody = new String(bytes, StandardCharsets.UTF_8);
//                        log.info("requestTime:{},requestParams:{}", requestTime, requestBody);
//
//                        //exchange.getAttributes().put("POST_BODY", requestBody);
//                        DataBufferUtils.release(dataBuffer);
//                        Flux<DataBuffer> cachedFlux = Flux.defer(() -> {
//                            DataBuffer buffer = exchange.getResponse().bufferFactory()
//                                    .wrap(bytes);
//                            return Mono.just(buffer);
//                        });
//
//                        ServerHttpRequest mutatedRequest = new ServerHttpRequestDecorator(
//                                exchange.getRequest()) {
//                            @Override
//                            public Flux<DataBuffer> getBody() {
//                                return cachedFlux;
//                            }
//                        };
//                        return chain.filter(exchange.mutate().request(mutatedRequest)
//                                .build());
//                    });
//        } else if (HttpMethod.GET.matches(method)) {
//            MultiValueMap<String, String> requestParams = serverHttpRequest.getQueryParams();
//            log.info("requestTime:{},requestParams:{}", requestTime, requestParams.toString());
//        }
//        return chain.filter(exchange);
//    }
//
//    @Override
//    public int getOrder() {
//        return ROUTE_TO_URL_FILTER_ORDER;
//    }
//}
