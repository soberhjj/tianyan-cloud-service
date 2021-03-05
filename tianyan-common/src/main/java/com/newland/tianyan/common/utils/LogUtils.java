package com.newland.tianyan.common.utils;


import org.slf4j.MDC;
import org.springframework.stereotype.Component;

import java.util.Random;
import java.util.UUID;


/**
 * @author newland
 */
@Component
public class LogUtils {

    /**
     * 生成traceId ，requestId，spanId 类似，设置不同的方法名即可
     */
    public static String traceId() {
        //return UUID.randomUUID().toString() + new Random().nextInt(1000000);
        return MDC.get("traceId");
    }
}
