package com.newland.tianyan.commons.webcore.filter;


import com.newland.tianya.commons.base.constants.GlobalExceptionEnum;
import com.newland.tianya.commons.base.constants.GlobalTraceConstant;
import com.newland.tianya.commons.base.support.ExceptionSupport;
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

import java.util.Map;


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
        if (!mediaType.equals(MediaType.APPLICATION_JSON) || o instanceof Exception) {
            log.info("responseParams：{}", JsonUtils.toJson(o));

            if (o instanceof Exception) {
                GlobalExceptionEnum errorEnums = null;
                if ("用户名或密码错误".equals(((Exception) o).getMessage())) {
                    errorEnums = GlobalExceptionEnum.CLIENT_SECRET_ERROR;
                }
                if (((Exception) o).getMessage().contains("Unauthorized grant type") || ((Exception) o).getMessage().contains("Unsupported grant type")) {
                    errorEnums = GlobalExceptionEnum.GRANT_TYPE_INVALID;
                }
                if (errorEnums == null) {
                    errorEnums = GlobalExceptionEnum.SYSTEM_ERROR;
                }
                return ResponseBodyConvert.toSnakeCaseObject(ExceptionSupport.toException(errorEnums));
            }
        } else {
            Map<String, Object> map = JsonUtils.toMap(o);
            if (map != null) {
                if (map.containsKey("image")) {
                    map.remove("image");
                    map.put("image", "(base转码图片，省略不打印)");
                }
                log.info("responseParams：{}", JsonUtils.toJson(map));
            }
        }

        return o;
    }
}
