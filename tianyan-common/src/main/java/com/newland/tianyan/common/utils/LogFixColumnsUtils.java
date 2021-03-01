package com.newland.tianyan.common.utils;

import org.slf4j.MDC;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;
import java.net.UnknownHostException;

import static com.newland.tianyan.common.constans.GlobalTraceConstant.TRACE_MDC;

/**
 * @author: RojiaHuang
 * @description:
 * @date: 2021/2/25
 */
@Component
public class LogFixColumnsUtils {

    public void init(String traceId, String url, String clientIp, String serverAddress) throws UnknownHostException {
        MDC.put(TRACE_MDC, traceId);
        MDC.put("uri", url);
        MDC.put("responseId", serverAddress);
        MDC.put("requestIp", clientIp);
    }

    public void clear() {
        MDC.remove("gatewayTraceId");
        MDC.remove("uri");
        MDC.remove("responseId");
        MDC.remove("requestIp");
    }
}
