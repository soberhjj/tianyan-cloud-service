package com.newland.tianya.commons.base.utils;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.PropertyNamingStrategy;
import com.alibaba.fastjson.serializer.SerializeConfig;
import com.alibaba.fastjson.serializer.SerializerFeature;

import java.util.Map;

/**
 * @author: RojiaHuang
 * @description:
 * @date: 2021/1/12
 */
public class JsonUtils {
    public static boolean isValidJson(Object object) {
        return JSON.isValid(object.toString());
    }

    /**
     * 默认驼峰
     */
    public static String toJson(Object jsonElement) {
        return JSON.toJSONString(jsonElement, SerializerFeature.IgnoreNonFieldGetter);
    }

    public static Map<String, Object> toMap(Object object) {
        return JSONObject.parseObject(JSONObject.toJSONString(object));
    }

    public static Object toObject(Object object) {
        return JSONObject.parseObject(JSONObject.toJSONString(object));
    }

    public static <T> T parseObject(String jsonString, Class<T> clazz) {
        return JSONObject.parseObject(jsonString, clazz);
    }

    /**
     * 下划线
     */
    public static String toSnakeCaseJsonString(Object jsonElement) {
        return JSON.toJSONString(jsonElement, getSnakeConfig(), SerializerFeature.IgnoreNonFieldGetter);
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
        String source = "{\n" +
                "    \"account\": \"huangtest\",\n" +
                "    \"app_id\": 2.2,\n" +
                "    \"client_id\": \"1,2,3,4\",\n" +
                "    \"client_secret\": \"新框架测试\"\n" +
                "}";
    }
}
