package com.newland.tianyan.gateway.filter;

import com.newland.tianyan.gateway.utils.LogFixColumnsUtils;
import com.newland.tianyan.gateway.utils.ReactiveAddrUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.skywalking.apm.toolkit.trace.TraceContext;
import org.slf4j.MDC;
import org.springframework.http.HttpHeaders;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.util.CollectionUtils;
import org.springframework.web.server.ServerWebExchange;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static com.newland.tianyan.gateway.constant.GlobalTraceConstant.GATEWAY_TRACE_HEAD;
import static com.newland.tianyan.gateway.constant.GlobalTraceConstant.TRACE_MDC;

/**
 * @author: RojiaHuang
 * @description:
 * @date: 2021/3/11
 */
@Slf4j
public class LoggingUtils {

    public void loggingReq(ServerWebExchange exchange, HttpHeaders httpHeaders, String requestParam) {
        List<String> traceIdList = getGlobalTraceId();
        if (!CollectionUtils.isEmpty(traceIdList)) {
            //固定列
            this.fixColumns(exchange.getRequest(), traceIdList.get(0));
            //traceId
            httpHeaders.put(GATEWAY_TRACE_HEAD, traceIdList);
            //requestTime
            String requestTime = LocalDateTime.now().toString();
            exchange.getAttributes().put("requestTime", requestTime);
            //打印日志
            log.info("requestTime:{},requestParam:{}", requestTime, requestParam);
        }
    }

    public void fixColumns(ServerHttpRequest serverHttpRequest, String traceId) {
        String url = serverHttpRequest.getURI().getPath();
        String clientIp = ReactiveAddrUtils.getRemoteAddr(serverHttpRequest);
        String serverIp = ReactiveAddrUtils.getLocalAddr();
        MDC.put(TRACE_MDC, traceId);
        LogFixColumnsUtils.init(url, clientIp, serverIp);
    }

    public List<String> getGlobalTraceId() {
        String traceId = TraceContext.traceId();
        List<String> traceIdList = new ArrayList<>();
        if (!"N/A".equals(traceId) && !"Ignored_Trace".equals(traceId)) {
            traceIdList.add(traceId);
        }
        return traceIdList;
    }
}
