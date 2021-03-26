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
        String url = serverHttpRequest.getURI().getPath();
        String clientIp = ReactiveAddrUtils.getRemoteAddr(serverHttpRequest);
        String serverIp = ReactiveAddrUtils.getLocalAddr();

        LogFixColumnUtils.init(url, clientIp, serverIp);
        ServerHttpResponse serverHttpResponse = exchange.getResponse();
        //traceId
        LogFixColumnUtils.init(this.addTradeIdToHeads(httpHeaders, serverHttpResponse));
        log.info("requestTime:{},requestParam:{}", this.addRequestTimeToHeads(exchange.getResponse()), JsonSkipSupport.toJson(requestParam));
    }

    public String addTradeIdToHeads(HttpHeaders httpHeaders, ServerHttpResponse serverHttpResponse) {
        if (httpHeaders.containsKey(GATEWAY_TRACE_HEAD)) {
            return httpHeaders.getFirst(GATEWAY_TRACE_HEAD);
        }
        String traceId = LogIdUtils.traceId();

        //透传到下游微服务
        httpHeaders.put(GATEWAY_TRACE_HEAD, Collections.singletonList(traceId));
        //透传到下一个后置filter的head中
        serverHttpResponse.getHeaders().put(GATEWAY_TRACE_HEAD, Collections.singletonList(traceId));
        return traceId;
    }

    public String getTradeIdFromHeads(ServerHttpResponse serverHttpRequest) {
        HttpHeaders headers = serverHttpRequest.getHeaders();
        return headers.containsKey(GATEWAY_TRACE_HEAD) ? headers.getFirst(GATEWAY_TRACE_HEAD) : null;
    }

    public String addRequestTimeToHeads(ServerHttpResponse serverHttpRequest) {
        String requestTime = LocalDateTime.now().toString();
        //透传到下一个后置filter的head中
        serverHttpRequest.getHeaders().put(HEAD_REQUEST_TIME, Collections.singletonList(requestTime));
        return requestTime;
    }

    public String getRequestTimeFromHeads(ServerHttpResponse serverHttpRequest) {
        HttpHeaders headers = serverHttpRequest.getHeaders();
        return headers.containsKey(HEAD_REQUEST_TIME) ? headers.getFirst(HEAD_REQUEST_TIME) : null;
    }

}
