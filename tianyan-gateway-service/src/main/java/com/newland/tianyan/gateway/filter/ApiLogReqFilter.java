package com.newland.tianyan.gateway.filter;

import com.newland.tianya.commons.base.utils.LogFixColumnUtils;
import com.newland.tianyan.gateway.utils.ReactiveAddrUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.skywalking.apm.toolkit.trace.TraceContext;
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
import org.springframework.util.CollectionUtils;
import org.springframework.web.reactive.function.BodyInserter;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;

import static com.newland.tianya.commons.base.constants.GlobalTraceConstant.GATEWAY_TRACE_HEAD;
import static org.springframework.cloud.gateway.filter.RouteToRequestUrlFilter.ROUTE_TO_URL_FILTER_ORDER;

/**
 * 会话请求信息打印，
 * 生成日志链路追踪TraceId并传入header中
 *
 * @author: RojiaHuang
 * @date: 2021/2/27
 */
@Component
@Slf4j
public class ApiLogReqFilter implements GlobalFilter, Ordered {

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
                                loggingReq(exchange, httpHeaders, requestParam.get());
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
        // get请求
        if (HttpMethod.GET.name().equalsIgnoreCase(method)) {
            List<String> traceIdList = getGlobalTraceId();
            if (!CollectionUtils.isEmpty(traceIdList)) {
                ServerHttpRequest httpRequest = exchange.getRequest().mutate()
                        .header(GATEWAY_TRACE_HEAD, getGlobalTraceId().get(0)).build();
                return chain.filter(exchange.mutate().request(httpRequest).build());
            } else {
                ServerHttpRequest httpRequest = exchange.getRequest().mutate().build();
                return chain.filter(exchange.mutate().request(httpRequest).build());
            }
        }
        return chain.filter(exchange);
    }

    private void loggingReq(ServerWebExchange exchange, HttpHeaders httpHeaders, String requestParam) {
        //全局traceId
        List<String> traceIdList = getGlobalTraceId();
        //固定列
        this.fixColumns(exchange.getRequest());
        //requestTime
        String requestTime = LocalDateTime.now().toString();
        if (!CollectionUtils.isEmpty(traceIdList)) {
            httpHeaders.put(GATEWAY_TRACE_HEAD, traceIdList);
            LogFixColumnUtils.init(traceIdList.get(0));
            //打印日志
            log.info("requestTime:{},requestParam:{}", requestTime, requestParam);
        }
    }

    private List<String> getGlobalTraceId() {
        String traceId = TraceContext.traceId();
        List<String> traceIdList = new ArrayList<>();
        String invalidTraceId = "N/A";
        if (!invalidTraceId.equals(traceId)) {
            traceIdList.add(traceId);
        }
        return traceIdList;
    }

    private void fixColumns(ServerHttpRequest serverHttpRequest) {
        String url = serverHttpRequest.getURI().getPath();
        String clientIp = ReactiveAddrUtils.getRemoteAddr(serverHttpRequest);
        String serverIp = ReactiveAddrUtils.getLocalAddr();

        LogFixColumnUtils.init(url, clientIp, serverIp);
    }


    @Override
    public int getOrder() {
        return ROUTE_TO_URL_FILTER_ORDER;
    }
}
