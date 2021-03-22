package com.newland.tianyan.face.aop;

import com.google.protobuf.Message;
import com.google.protobuf.util.JsonFormat;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.core.MethodParameter;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpInputMessage;
import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.protobuf.ProtobufJsonFormatHttpMessageConverter;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.mvc.method.annotation.RequestBodyAdvice;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.Type;

/**
 * 授权解析token信息统一填充请求体
 *
 * @Author: huangJunJie  2021-03-05 10:57
 */
@RestControllerAdvice
public class HeadParamToBodyAdvice implements RequestBodyAdvice {

    @Autowired
    private HttpServletRequest request;

    @Bean
    public ProtobufJsonFormatHttpMessageConverter protobufJsonFormatHttpMessageConverter() {
        JsonFormat.Printer printer = JsonFormat.printer().preservingProtoFieldNames();
        JsonFormat.Parser parser = JsonFormat.parser();
        return new ProtobufJsonFormatHttpMessageConverter(parser, printer) {

            @Override
            protected void addDefaultHeaders(HttpHeaders headers, Message message, MediaType contentType) throws IOException {
                super.addDefaultHeaders(headers, message, new MediaType("application", "json", DEFAULT_CHARSET));
            }
        };
    }

    @Override
    public boolean supports(MethodParameter methodParameter, Type type, Class<? extends HttpMessageConverter<?>> aClass) {
        return true;
    }

    @Override
    public HttpInputMessage beforeBodyRead(HttpInputMessage httpInputMessage, MethodParameter methodParameter, Type type, Class<? extends HttpMessageConverter<?>> aClass) throws IOException {
        return httpInputMessage;
    }

    @Override
    public Object afterBodyRead(Object body, HttpInputMessage httpInputMessage, MethodParameter methodParameter, Type type, Class<? extends HttpMessageConverter<?>> aClass) {
        Class clazz = body.getClass();
        String headerAccount = request.getHeader("account");
        String headerAppId = request.getHeader("app_id");

        for (Field field : clazz.getDeclaredFields()) {
            if ((!StringUtils.isEmpty(headerAccount)) && "account".equals(field.getName())) {
                setValue(body, field, headerAccount);
            }
            if ((!StringUtils.isEmpty(headerAppId)) && "appId".equals(field.getName())) {
                setValue(body, field, Long.valueOf(headerAppId));
            }
        }
        return body;
    }

    @Override
    public Object handleEmptyBody(Object o, HttpInputMessage httpInputMessage, MethodParameter methodParameter, Type type, Class<? extends HttpMessageConverter<?>> aClass) {
        return null;
    }

    private void setValue(Object body, Field field, Object value) {
        try {
            field.setAccessible(true);
            field.set(body, value);
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
    }
}
