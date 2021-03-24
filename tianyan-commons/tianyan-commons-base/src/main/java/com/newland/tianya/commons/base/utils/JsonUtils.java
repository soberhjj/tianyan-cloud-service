package com.newland.tianya.commons.base.utils;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.PropertyNamingStrategy;
import com.alibaba.fastjson.serializer.SerializeConfig;
import com.newland.tianya.commons.base.constants.ExceptionTypeEnums;

import java.util.Map;

/**
 * @author: RojiaHuang
 * @description:
 * @date: 2021/1/12
 */
public class JsonUtils {
    /**
     * 默认驼峰
     */
    public static String toJson(Object jsonElement) {
        return JSON.toJSONString(jsonElement);
    }

    public static Map<String, Object> toMap(Object object) {
        return JSONObject.parseObject(JSONObject.toJSONString(object));
    }

    public static Object toObject(Object object) {
        return JSONObject.parseObject(JSONObject.toJSONString(object));
    }

    /**
     * 下划线
     */
    public static String toSnakeCaseJsonString(Object jsonElement) {
        return JSON.toJSONString(jsonElement, getSnakeConfig());
    }

    public static Object toSnakeCaseObject(Object object) {
        return JSON.toJSON(object, getSnakeConfig());
    }

    private static SerializeConfig getSnakeConfig() {
        SerializeConfig serializeConfig = new SerializeConfig();
        serializeConfig.propertyNamingStrategy = PropertyNamingStrategy.SnakeCase;
        return serializeConfig;
    }

    public static void main(String[] args) {
        ExceptionTypeEnums exceptionTypeEnums = ExceptionTypeEnums.ARGUMENT_EXCEPTION;
        System.out.println(JsonUtils.toSnakeCaseJsonString(exceptionTypeEnums));
    }
}
