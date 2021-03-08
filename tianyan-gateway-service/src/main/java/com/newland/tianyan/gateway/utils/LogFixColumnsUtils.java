package com.newland.tianyan.gateway.utils;

import org.slf4j.MDC;
import org.springframework.stereotype.Component;

import java.net.UnknownHostException;

/**
 * @author: RojiaHuang
 * @description:
 * @date: 2021/2/27
 */
@Component
public class LogFixColumnsUtils {
    public static void init(String url, String clientIp, String serverAddress) {
        MDC.put("uri", url);
        MDC.put("responseId", serverAddress);
        MDC.put("requestIp", clientIp);
    }

    public static void clear() {
        MDC.remove("traceId");
        MDC.remove("uri");
        MDC.remove("responseId");
        MDC.remove("requestIp");
    }
}
