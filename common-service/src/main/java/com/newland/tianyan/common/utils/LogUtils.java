package com.newland.tianyan.common.utils;

import brave.Tracer;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * @author: RojiaHuang
 * @description:
 * @date: 2021/2/4
 */
public class LogUtils {
    //todo 更新为Tracer
    private static Tracer tracer;

    @Autowired
    public void setTracer(Tracer tracer) {
        LogUtils.tracer = tracer;
    }

    public static String getLogId() {
        return tracer.currentSpan().context().traceIdString();
    }
}
