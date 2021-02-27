package com.newland.tianyan.gateway.constant;

/**
 * @author: RojiaHuang
 * @description:
 * @date: 2021/2/26
 */
public class GlobalTraceConstant {
    /**
     * 从http header层面获取traceId的KEY值
     */
    public static final String GATEWAY_TRACE_HEAD = "GATEWAY_TRACE_HEAD";

    public static final String PARENT_SPAN_HEAD = "PARENT_SPAN_HEAD";

    public static final String SPAN_HEAD = "SPAN_HEAD";
    /**
     * 从mdc层面获取traceId的KEY值
     */
    public static final String TRACE_MDC = "tid";
}
