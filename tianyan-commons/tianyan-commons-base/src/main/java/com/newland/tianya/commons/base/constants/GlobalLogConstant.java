package com.newland.tianya.commons.base.constants;

/**
 * @author: RojiaHuang
 * @description:
 * @date: 2021/2/26
 */
public class GlobalLogConstant {
    /**
     * 从http header层面获取traceId的KEY值
     */
    public static final String GATEWAY_TRACE_HEAD = "Gateway-Trace-Id";

    /**
     * 从mdc层面获取traceId的KEY值
     */
    public static final String TRACE_MDC = "traceId";

    public static final String HEAD_REQUEST_TIME = "Request-Time";
}
