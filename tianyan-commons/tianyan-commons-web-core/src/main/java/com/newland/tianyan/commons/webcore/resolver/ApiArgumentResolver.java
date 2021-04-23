package com.newland.tianyan.commons.webcore.resolver;

import com.newland.tianyan.commons.webcore.support.ValidatorSupport;
import org.springframework.core.MethodParameter;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;

import javax.validation.Valid;

/**
 * @author: RojiaHuang
 * @description:
 * @date: 2021/4/21
 */
@Component
public class ApiArgumentResolver implements HandlerMethodArgumentResolver {
    @Override
    public boolean supportsParameter(MethodParameter parameter) {
        System.out.println("ApiArgumentResolver=supportsParameter");
        return parameter.getParameterType().isAnnotationPresent(Valid.class);
    }

    @Override
    public Object resolveArgument(MethodParameter parameter, ModelAndViewContainer mavContainer,
                                  NativeWebRequest webRequest, WebDataBinderFactory binderFactory){
        if (parameter.hasParameterAnnotation(Valid.class)) {
            ValidatorSupport.validate(parameter);
        }
        return parameter;
    }
}
