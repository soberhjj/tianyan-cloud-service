package com.newland.tianyan.auth.compoment;

import org.slf4j.MDC;
import org.springframework.core.task.TaskDecorator;

import java.util.Map;

/**
 * @author: RojiaHuang
 * @description:
 * @date: 2021/2/23
 */
public class MdcTaskDecorator implements TaskDecorator {
    @Override
    public Runnable decorate(Runnable runnable) {
        Map<String, String> contextMap = MDC.getCopyOfContextMap();

        return () -> {
            try {
                // @Async thread context
                // Restore the web thread MDC context
                if(contextMap != null) {
                    MDC.setContextMap(contextMap);
                }
                else {
                    MDC.clear();
                }

                // Run the new thread
                runnable.run();
            }
            finally {
                MDC.clear();
            }
        };
    }
}
