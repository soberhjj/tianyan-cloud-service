package com.newland.tianyan.common.aop;

import com.newland.tianyan.common.utils.JsonUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.skywalking.apm.toolkit.trace.TraceContext;
import org.springframework.core.MethodParameter;
import org.springframework.http.MediaType;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.servlet.mvc.method.annotation.ResponseBodyAdvice;

import static com.newland.tianyan.common.constans.GlobalTraceConstant.GATEWAY_TRACE_HEAD;

/**
 * @author: RojiaHuang
 * @description:
 * @date: 2021/2/25
 */
@ControllerAdvice
@Slf4j
public class ApiRespBodyAdvice implements ResponseBodyAdvice {
    @Override
    public boolean supports(MethodParameter methodParameter, Class aClass) {
        return true;
    }

    @Override
    public Object beforeBodyWrite(Object o, MethodParameter methodParameter,
                                  MediaType mediaType, Class aClass,
                                  ServerHttpRequest serverHttpRequest, ServerHttpResponse serverHttpResponse) {
        log.info("responseParams：{}", JsonUtils.toJson(o));
        String traceId = TraceContext.traceId();
        serverHttpResponse.getHeaders().add(GATEWAY_TRACE_HEAD,traceId);
        return o;
    }
}
