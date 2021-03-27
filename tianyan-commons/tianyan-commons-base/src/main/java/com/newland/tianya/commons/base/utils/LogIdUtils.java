package com.newland.tianya.commons.base.utils;


import cn.hutool.core.util.IdUtil;
import com.newland.tianya.commons.base.generator.IDUtil;
import org.apache.skywalking.apm.toolkit.trace.TraceContext;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;


/**
 * @author newland
 */
@Component
public class LogIdUtils {

    /**
     * 生成traceId ，requestId，spanId 类似，设置不同的方法名即可
     */
    public static String traceId() {
        String traceId = TraceContext.traceId();
        String invalidTraceId1 = "N/A";
        String invalidTraceId2 = "Ignored_Trace";
        if (invalidTraceId1.equals(traceId) || invalidTraceId2.equals(traceId)
                || StringUtils.isEmpty(traceId)) {
            traceId = IdUtil.randomUUID();
        }
        return StringUtils.isEmpty(traceId) ? "" : traceId;
    }

    public static void main(String[] args) {
        System.out.println("begin：" + LogIdUtils.traceId());
    }
}
