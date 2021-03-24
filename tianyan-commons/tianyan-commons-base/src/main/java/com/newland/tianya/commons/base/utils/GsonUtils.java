package com.newland.tianya.commons.base.utils;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonSyntaxException;

import java.io.IOException;
import java.lang.reflect.Type;

/**
 * @author: RojiaHuang
 * @description:
 * @date: 2021/3/24
 */
public class GsonUtils {
    private static Gson g = new Gson();
    private static Gson g1 = (new GsonBuilder()).disableHtmlEscaping().create();
    private static ObjectMapper objectMapper = new ObjectMapper();

    public static <T> T fromJson(String json, Type typeOfT) throws JsonSyntaxException {
        return g.fromJson(json, typeOfT);
    }

    public static <T> T fromJson(String json, Class<T> classOfT) throws JsonSyntaxException {
        return g.fromJson(json, classOfT);
    }

    public static String toJson(Object jsonElement) {
        return g.toJson(jsonElement);
    }

    public static String toJsonNoCode(Object jsonElement) {
        return g1.toJson(jsonElement);
    }

    public static String writeValueAsString(Object value) {
        try {
            return objectMapper.writeValueAsString(value);
        } catch (JsonProcessingException var2) {
            throw new RuntimeException("对象转为json失败", var2);
        }
    }

    public static <T> T readValue(String json, Class<T> classOfT) {
        try {
            return objectMapper.readValue(json, classOfT);
        } catch (IOException var3) {
            throw new RuntimeException("json转对象失败", var3);
        }
    }

}
