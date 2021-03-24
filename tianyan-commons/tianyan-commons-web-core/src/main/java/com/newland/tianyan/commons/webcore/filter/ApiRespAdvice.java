package com.newland.tianyan.commons.webcore.filter;


import com.newland.tianya.commons.base.constants.GlobalTraceConstant;
import com.newland.tianya.commons.base.support.ExceptionSupport;
import com.newland.tianya.commons.base.support.JsonSkipSupport;
import com.newland.tianya.commons.base.support.ResponseBodyConvert;
import com.newland.tianya.commons.base.utils.JsonUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.skywalking.apm.toolkit.trace.TraceContext;
import org.springframework.core.MethodParameter;
import org.springframework.http.MediaType;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.mvc.method.annotation.ResponseBodyAdvice;


/**
 * @author: RojiaHuang
 * @description:
 * @date: 2021/2/25
 */
@RestControllerAdvice
@Slf4j
public class ApiRespAdvice implements ResponseBodyAdvice {

    @Override
    public boolean supports(MethodParameter methodParameter, Class aClass) {
        return true;
    }

    @Override
    public Object beforeBodyWrite(Object o, MethodParameter methodParameter,
                                  MediaType mediaType, Class aClass,
                                  ServerHttpRequest serverHttpRequest, ServerHttpResponse serverHttpResponse) {
        String traceId = TraceContext.traceId();
        serverHttpResponse.getHeaders().add(GlobalTraceConstant.GATEWAY_TRACE_HEAD, traceId);
        log.info("responseParams：{}", JsonSkipSupport.toJson(o));
        return o;
    }
}
