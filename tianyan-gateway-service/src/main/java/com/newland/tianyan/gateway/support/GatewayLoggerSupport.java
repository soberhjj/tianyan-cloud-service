package com.newland.tianyan.gateway.support;

import com.newland.tianya.commons.base.support.JsonSkipSupport;
import com.newland.tianya.commons.base.utils.LogFixColumnUtils;
import com.newland.tianya.commons.base.utils.LogIdUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.web.server.ServerWebExchange;

import java.time.LocalDateTime;
import java.util.Collections;

import static com.newland.tianya.commons.base.constants.GlobalLogConstant.GATEWAY_TRACE_HEAD;
import static com.newland.tianya.commons.base.constants.GlobalLogConstant.HEAD_REQUEST_TIME;
import static com.newland.tianya.commons.base.constants.TokenConstants.HEAD_ACCOUNT;
import static com.newland.tianya.commons.base.constants.TokenConstants.HEAD_APP_ID;

/**
 * @author: RojiaHuang
 * @description:
 * @date: 2021/3/25
 */
@Slf4j
public class GatewayLoggerSupport {

    public void initAndLoggingReqMsg(ServerWebExchange exchange, HttpHeaders httpHeaders, String requestParam) {
        //固定列
        ServerHttpRequest serverHttpRequest = exchange.getRequest();
        //traceId
        LogFixColumnUtils.init(LogFixColumnUtils.LogFixColumn.builder()
                .url(serverHttpRequest.getURI().getPath())
                .clientIp(ReactiveAddrUtils.getRemoteAddr(serverHttpRequest))
                .serverAddress(ReactiveAddrUtils.getLocalAddr())
                .traceId(this.addTradeIdToHeads(httpHeaders))
                .account(httpHeaders.getFirst(HEAD_ACCOUNT))
                .appId(httpHeaders.getFirst(HEAD_APP_ID))
                .build());
        log.info("requestTime:{},requestParam:{}", this.getRequestTimeFromHeads(serverHttpRequest), JsonSkipSupport.toJson(requestParam));
    }

    public String addTradeIdToHeads(HttpHeaders httpHeaders) {
        if (httpHeaders.containsKey(GATEWAY_TRACE_HEAD)) {
            return httpHeaders.getFirst(GATEWAY_TRACE_HEAD);
        }
        String traceId = LogIdUtils.traceId();

        //透传到下游微服务
        httpHeaders.put(GATEWAY_TRACE_HEAD, Collections.singletonList(traceId));
        return traceId;
    }

    public String getTradeIdFromHeads(ServerHttpResponse serverHttpRequest) {
        HttpHeaders headers = serverHttpRequest.getHeaders();
        return headers.containsKey(GATEWAY_TRACE_HEAD) ? headers.getFirst(GATEWAY_TRACE_HEAD) : null;
    }

    public String getRequestTimeFromHeads(ServerHttpRequest serverHttpRequest) {
        HttpHeaders headers = serverHttpRequest.getHeaders();
        return headers.containsKey(HEAD_REQUEST_TIME) ? headers.getFirst(HEAD_REQUEST_TIME) : null;
    }

}
