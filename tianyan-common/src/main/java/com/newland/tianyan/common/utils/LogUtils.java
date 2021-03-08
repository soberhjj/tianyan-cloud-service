package com.newland.tianyan.common.utils;


import org.apache.commons.lang3.StringUtils;
import org.slf4j.MDC;
import org.springframework.stereotype.Component;


/**
 * @author newland
 */
@Component
public class LogUtils {

    /**
     * 生成traceId ，requestId，spanId 类似，设置不同的方法名即可
     */
    public static String traceId() {
        String traceId = MDC.get("traceId");
        return StringUtils.isEmpty(traceId) ? "" : traceId;
    }
}
