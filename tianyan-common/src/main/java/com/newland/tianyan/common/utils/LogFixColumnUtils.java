package com.newland.tianyan.common.utils;

import org.slf4j.MDC;
import org.springframework.stereotype.Component;

import java.net.UnknownHostException;

/**
 * @author: RojiaHuang
 * @description:
 * @date: 2021/2/25
 */
@Component
public class LogFixColumnUtils {

    public void init(String traceId, String url, String clientIp, String serverAddress) throws UnknownHostException {
        MDC.put("traceId", traceId);
        MDC.put("uri", url);
        MDC.put("responseId", serverAddress);
        MDC.put("requestIp", clientIp);
    }

    public void clear() {
        MDC.remove("traceId");
        MDC.remove("uri");
        MDC.remove("responseId");
        MDC.remove("requestIp");
    }
}
