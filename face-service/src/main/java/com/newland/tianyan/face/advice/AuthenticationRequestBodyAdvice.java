package com.newland.tianyan.face.advice;

import com.google.protobuf.Message;
import com.google.protobuf.util.JsonFormat;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.core.MethodParameter;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpInputMessage;
import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.protobuf.ProtobufJsonFormatHttpMessageConverter;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.common.OAuth2AccessToken;
import org.springframework.security.oauth2.provider.OAuth2Authentication;
import org.springframework.security.oauth2.provider.authentication.OAuth2AuthenticationDetails;
import org.springframework.security.oauth2.provider.token.TokenStore;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.mvc.method.annotation.RequestBodyAdvice;

import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

@RestControllerAdvice
public class AuthenticationRequestBodyAdvice implements RequestBodyAdvice {

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

    @Autowired
    private TokenStore jwtTokenStore;

    @Override
    public boolean supports(MethodParameter methodParameter, Type targetType, Class<? extends HttpMessageConverter<?>> converterType) {
        return true;
    }

    @Override
    public HttpInputMessage beforeBodyRead(HttpInputMessage inputMessage, MethodParameter parameter, Type targetType,
                                           Class<? extends HttpMessageConverter<?>> converterType) throws IOException {
        return inputMessage;
    }

    @Override
    public Object afterBodyRead(Object body, HttpInputMessage inputMessage, MethodParameter parameter, Type targetType,
                                Class<? extends HttpMessageConverter<?>> converterType) {

        Class clazz = body.getClass();

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();

        if (auth instanceof AnonymousAuthenticationToken) {
            return body;
        }

        OAuth2Authentication authentication = (OAuth2Authentication) auth;

        List<String> fields = new ArrayList<>(Collections.singletonList("account"));
        OAuth2AuthenticationDetails details = (OAuth2AuthenticationDetails) auth.getDetails();


        OAuth2AccessToken accessToken = jwtTokenStore.readAccessToken(details.getTokenValue());
        Map<String, Object> information = accessToken.getAdditionalInformation();
        String grantType = (String) information.get("grant_type");

        // 使用客户端模式时,只能获取该客户端对应的 APP_ID（对外接口）
        if (grantType.equals("client_credentials")) {
            fields.add("appId");
        }

        // 密码模式获取account（对内接口）
        if (grantType.equals("password")) {
            information.put("account", authentication.getName());
        }

        for (Field field : clazz.getDeclaredFields()) {
            if (fields.contains(field.getName())) {
                try {
                    Field accountField = clazz.getDeclaredField(field.getName());
                    setValue(body, accountField, information.get(field.getName()));
                } catch (NoSuchFieldException e) {
                    e.printStackTrace();
                    return body;
                }
            }
        }

        return body;
    }

    @Override
    public Object handleEmptyBody(Object body, HttpInputMessage inputMessage, MethodParameter parameter, Type
            targetType, Class<? extends HttpMessageConverter<?>> converterType) {
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