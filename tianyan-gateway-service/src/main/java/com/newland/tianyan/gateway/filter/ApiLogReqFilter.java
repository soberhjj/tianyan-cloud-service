package com.newland.tianyan.gateway.filter;

import com.newland.tianyan.gateway.support.GatewayLoggerSupport;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.cloud.gateway.filter.factory.rewrite.CachedBodyOutputMessage;
import org.springframework.cloud.gateway.support.BodyInserterContext;
import org.springframework.cloud.gateway.support.DefaultServerRequest;
import org.springframework.core.Ordered;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpRequestDecorator;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.BodyInserter;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;

import static org.springframework.cloud.gateway.filter.RouteToRequestUrlFilter.ROUTE_TO_URL_FILTER_ORDER;

/**
 * 会话请求信息打印，
 * 生成日志链路追踪TraceId并传入header中
 * <p>
 * 改造逻辑来源：ModifyRequestBodyGatewayFilterFactory
 *
 * @author: RojiaHuang
 * @date: 2021/2/27
 */
@Component
@Slf4j
public class ApiLogReqFilter extends GatewayLoggerSupport implements GlobalFilter, Ordered {

    private static final String CONTENT_TYPE = "Content-Type";

    private static final String CONTENT_TYPE_JSON = "application/json";

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        ServerHttpRequest serverHttpRequest = exchange.getRequest();
        String contentType = serverHttpRequest.getHeaders().getFirst(CONTENT_TYPE);
        String method = serverHttpRequest.getMethodValue();
        AtomicReference<String> requestParam = new AtomicReference<>("");
        // post且json请求
        if (HttpMethod.POST.name().equalsIgnoreCase(method) && null != contentType && contentType.contains(CONTENT_TYPE_JSON)) {
            ServerRequest serverRequest = new DefaultServerRequest(exchange);
            Mono<String> modifiedBody = serverRequest.bodyToMono(String.class)
                    .flatMap(body -> {
                        requestParam.set(body);
                        return Mono.just(body);
                    });

            AtomicInteger getHeadersCount = new AtomicInteger(0);
            BodyInserter bodyInserter = BodyInserters.fromPublisher(modifiedBody, String.class);
            HttpHeaders headers = new HttpHeaders();
            headers.putAll(exchange.getRequest().getHeaders());
            // the new content type will be computed by bodyInserter
            // and then set in the request decorator
            headers.remove(HttpHeaders.CONTENT_LENGTH);
            CachedBodyOutputMessage outputMessage = new CachedBodyOutputMessage(
                    exchange, headers);
            return bodyInserter.insert(outputMessage, new BodyInserterContext())
                    .then(Mono.defer(() -> {
                        ServerHttpRequest decorator = new ServerHttpRequestDecorator(
                                exchange.getRequest()) {
                            @Override
                            public HttpHeaders getHeaders() {
                                long contentLength = headers.getContentLength();
                                HttpHeaders httpHeaders = new HttpHeaders();
                                httpHeaders.putAll(super.getHeaders());
                                int doInitLog = 2;
                                if (getHeadersCount.incrementAndGet() == doInitLog) {
                                    initAndLoggingReqMsg(exchange, httpHeaders, requestParam.get());
                                }
                                if (contentLength > 0) {
                                    httpHeaders.setContentLength(contentLength);
                                } else {
                                    httpHeaders.set(HttpHeaders.TRANSFER_ENCODING, "chunked");
                                }
                                return httpHeaders;
                            }

                            @Override
                            public Flux<DataBuffer> getBody() {
                                return outputMessage.getBody();
                            }

                        };
                        return chain.filter(exchange.mutate().request(decorator).build());
                    }));
        }
        return chain.filter(exchange);
    }

    @Override
    public int getOrder() {
        return ROUTE_TO_URL_FILTER_ORDER;
    }
}
