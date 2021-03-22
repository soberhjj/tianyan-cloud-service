package com.newland.tianya.commons.base.utils;


import org.apache.skywalking.apm.toolkit.trace.TraceContext;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;


/**
 * @author newland
 */
@Component
public class LogUtils {

    /**
     * 生成traceId ，requestId，spanId 类似，设置不同的方法名即可
     */
    public static String traceId() {
        String traceId = TraceContext.traceId();
        return StringUtils.isEmpty(traceId) ? "" : traceId;
    }
}
