package com.newland.tianyan.common.utils.utils;

import brave.Tracer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;


/**
 * @author newland
 */
@Component
public class LogUtils {

    private static Tracer tracer;

    @Autowired
    public void setTracer(Tracer tracer) {
        LogUtils.tracer = tracer;
    }

    public static String getLogId() {
        return tracer.currentSpan().context().traceIdString();
    }

}
