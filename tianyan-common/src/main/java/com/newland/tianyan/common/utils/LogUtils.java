package com.newland.tianyan.common.utils;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Random;
import java.util.UUID;


/**
 * @author newland
 */
@Component
public class LogUtils {

//    private static Tracer tracer;
//
//    @Autowired
//    public void setTracer(Tracer tracer) {
//        LogUtils.tracer = tracer;
//    }
//
//    public static String getLogId() {
//        return tracer.currentSpan().context().traceIdString();
//    }

        public static String getLogId() {
        return traceId();
    }
    /**
     * 生成traceId ，requestId，spanId 类似，设置不同的方法名即可
     */
    public static String traceId() {
        return UUID.randomUUID().toString() + new Random().nextInt(1000000);
    }
}
