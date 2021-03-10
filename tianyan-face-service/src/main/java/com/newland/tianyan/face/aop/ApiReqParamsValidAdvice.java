package com.newland.tianyan.face.aop;

import com.newland.tianyan.face.constant.ArgumentErrorEnums;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.MethodParameter;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpInputMessage;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.servlet.mvc.method.annotation.RequestBodyAdvice;

import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Field;
import java.lang.reflect.Type;

import static com.newland.tianyan.face.constant.BusinessArgumentConstants.*;

/**
 * @author: RojiaHuang
 * @description:
 * @date: 2021/2/25
 */
@ControllerAdvice
@Slf4j
public class ApiReqParamsValidAdvice implements RequestBodyAdvice {
    @Override
    public boolean supports(MethodParameter methodParameter, Type type, Class<? extends HttpMessageConverter<?>> aClass) {
        return true;
    }

    @Override
    public HttpInputMessage beforeBodyRead(HttpInputMessage httpInputMessage, MethodParameter methodParameter, Type type, Class<? extends HttpMessageConverter<?>> aClass) throws IOException {
        return new HttpInputMessage() {
            @Override
            public InputStream getBody() throws IOException {
                return httpInputMessage.getBody();
            }

            @Override
            public HttpHeaders getHeaders() {
                return httpInputMessage.getHeaders();
            }
        };
    }

    @SneakyThrows
    @Override
    public Object afterBodyRead(Object o, HttpInputMessage httpInputMessage, MethodParameter methodParameter, Type type, Class<? extends HttpMessageConverter<?>> aClass) {
        Class objClass = o.getClass();
        String[] checkFields = new String[]{"faceFields"};
        for (Field field : objClass.getDeclaredFields()) {
            for (String fieldName : checkFields) {
                if (fieldName.equals(field.getName())) {
                    if (!field.isAccessible()) {
                        field.setAccessible(true);
                    }
                    match(fieldName, field.get(o).toString());
                }
            }
        }
        return o;
    }

    private void match(String fieldName, String value) {
        String[] valueArr = value.split(",");
        if ("faceFields".equals(fieldName)) {
            for (String valueItem : valueArr) {
                boolean coordinate = FACE_FIELD_COORDINATE.equals(valueItem);
                boolean liveNess = FACE_FIELD_LIVENESS.equals(valueItem);
                if ((!coordinate) && (!liveNess)) {
                    throw ArgumentErrorEnums.WRONG_FACE_FIELD.toException();
                }
            }
        }
        if ("actionType".equals(fieldName)) {
            for (String valueItem : valueArr) {
                boolean append = ACTION_TYPE_APPEND.equals(valueItem);
                boolean replace = ACTION_TYPE_REPLACE.equals(valueItem);
                if ((!append) && (!replace)) {
                    throw ArgumentErrorEnums.WRONG_ACTION_TYPE.toException();
                }
            }
        }
    }

    @Override
    public Object handleEmptyBody(Object o, HttpInputMessage httpInputMessage, MethodParameter methodParameter, Type type, Class<? extends HttpMessageConverter<?>> aClass) {
        return o;
    }
}
