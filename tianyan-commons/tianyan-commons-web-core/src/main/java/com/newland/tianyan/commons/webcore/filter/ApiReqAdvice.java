package com.newland.tianyan.commons.webcore.filter;

import com.newland.tianya.commons.base.support.JsonSkipSupport;
import com.newland.tianya.commons.base.utils.GsonUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.IOUtils;
import org.springframework.core.MethodParameter;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpInputMessage;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.servlet.mvc.method.annotation.RequestBodyAdvice;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Type;
import java.nio.charset.StandardCharsets;

/**
 * @author: RojiaHuang
 * @description:
 * @date: 2021/2/25
 */
@ControllerAdvice
@Slf4j
public class ApiReqAdvice implements RequestBodyAdvice {
    @Override
    public boolean supports(MethodParameter methodParameter, Type type, Class<? extends HttpMessageConverter<?>> aClass) {
        return true;
    }

    @Override
    public HttpInputMessage beforeBodyRead(HttpInputMessage httpInputMessage, MethodParameter methodParameter, Type type, Class<? extends HttpMessageConverter<?>> aClass) throws IOException {
        String objectString = parseJsonToObject(httpInputMessage, type);

        return new HttpInputMessage() {
            @Override
            public InputStream getBody() throws IOException {
                return IOUtils.toInputStream(objectString, StandardCharsets.UTF_8);
            }

            @Override
            public HttpHeaders getHeaders() {
                return httpInputMessage.getHeaders();
            }
        };
    }


    @Override
    public Object afterBodyRead(Object o, HttpInputMessage httpInputMessage, MethodParameter methodParameter, Type type, Class<? extends HttpMessageConverter<?>> aClass) {
        log.info("requestParams：{}", JsonSkipSupport.toJson(o));
        return o;
    }

    @Override
    public Object handleEmptyBody(Object o, HttpInputMessage httpInputMessage, MethodParameter methodParameter, Type type, Class<? extends HttpMessageConverter<?>> aClass) {
        return o;
    }

    private String parseJsonToObject(HttpInputMessage httpInputMessage, Type type) throws IOException {
        InputStream inputStream = httpInputMessage.getBody();
        byte[] data = new byte[inputStream.available()];

        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        for (int len; (len = inputStream.read(data)) > 0; ) {
            byteArrayOutputStream.write(data, 0, len);
        }
        String string = byteArrayOutputStream.toString("utf-8");
        String json = convert(string);
        GsonUtils.fromJson(json, type);
        return string;
    }

    public static String convert(String defaultName) {
        char[] arr = defaultName.toCharArray();
        StringBuilder nameToReturn = new StringBuilder();

        for (int i = 0; i < arr.length; i++) {
            if (arr[i] == '_') {
                nameToReturn.append(Character.toUpperCase(arr[i + 1]));
                i++;
            } else {
                nameToReturn.append(arr[i]);
            }
        }
        return nameToReturn.toString();
    }

}
