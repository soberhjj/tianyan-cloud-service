package com.newland.tianya.commons.base.utils;

import lombok.Builder;
import lombok.Data;
import org.slf4j.MDC;
import org.springframework.stereotype.Component;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;

/**
 * @author: RojiaHuang
 * @description:
 * @date: 2021/2/25
 */
@Component
public class LogFixColumnUtils {

    @Builder
    @Data
    public static class LogFixColumn {
        String traceId;
        String url;
        String clientIp;
        String serverAddress;
        String account;
        String appId;

        public Map<String, String> parseMap() {
            Map<String, String> map = new HashMap<>(6);
            Field[] fields = LogFixColumn.class.getDeclaredFields();
            try {
                for (Field field : fields) {
                    if (field.get(this) != null) {
                        map.put(field.getName(), field.get(this).toString());
                    }
                }
            } catch (IllegalAccessException exception) {
                exception.printStackTrace();
            }
            return map;
        }
    }

    public static void init(LogFixColumn logFixColumn) {
        Map<String, String> map = logFixColumn.parseMap();
        map.keySet().forEach(item -> {
            MDC.put(item, map.get(item));
        });
    }

    public static void main(String[] args) {
        LogFixColumnUtils.init(LogFixColumnUtils.LogFixColumn.builder()
                .url("testurl")
                .traceId("testtraceId")
                .build());
    }
}
