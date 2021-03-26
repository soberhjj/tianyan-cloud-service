package com.newland.tianyan.gateway.support;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.newland.tianya.commons.base.utils.JsonUtils;
import org.springframework.util.StringUtils;

/**
 * @author: RojiaHuang
 * @description:
 * @date: 2021/3/26
 */
public class ResponseBodyTraceIdDecorator {
    public static String LOG_TRACE_ID_FIELD_NAME = "log_id";

    public static String LEFT_BRACKETS = "{";

    public static String putTraceId(String sourceBody, String traceId) {
        if (!sourceBody.contains(LOG_TRACE_ID_FIELD_NAME)) {
            return sourceBody;
        }

        if (StringUtils.isEmpty(traceId)) {
            return sourceBody;
        }

        String copySourceBody = sourceBody;
        if (!JsonUtils.isValidJson(sourceBody)) {
            if (sourceBody.indexOf(LEFT_BRACKETS) == 0) {
                StringBuilder stringBuffer = new StringBuilder();
                String onlyLogIdJson = sourceBody.split(",")[0];
                stringBuffer.append(onlyLogIdJson);
                stringBuffer.append("}");
                copySourceBody = stringBuffer.toString();
            } else {
                return sourceBody;
            }
        }

        JSONObject targetBody = JSON.parseObject(copySourceBody);
        String oldTraceId = (String) targetBody.get(LOG_TRACE_ID_FIELD_NAME);

        return sourceBody.replace(oldTraceId, traceId);
    }

    public static void main(String[] args) {
        String result = ResponseBodyTraceIdDecorator.putTraceId("{\n" +
                "  \"log_id\": \"825014713837420544\",\n" +
                "  \"count\": 10,", "123");
        System.out.println(result);
    }
}
