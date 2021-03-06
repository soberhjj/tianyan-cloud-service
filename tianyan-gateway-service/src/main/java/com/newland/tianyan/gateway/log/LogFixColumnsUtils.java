package com.newland.tianyan.gateway.log;

import org.slf4j.MDC;

import java.net.UnknownHostException;

/**
 * @author: RojiaHuang
 * @description:
 * @date: 2021/2/27
 */
public class LogFixColumnsUtils {
    public static void init(String url, String clientIp, String serverAddress) throws UnknownHostException {
        MDC.put("uri", url);
        MDC.put("responseId", serverAddress);
        MDC.put("requestIp", clientIp);
    }

    public static void clear() {
        MDC.remove("uri");
        MDC.remove("responseId");
        MDC.remove("requestIp");
    }
}
