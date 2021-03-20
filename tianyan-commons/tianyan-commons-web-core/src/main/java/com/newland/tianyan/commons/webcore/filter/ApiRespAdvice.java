package com.newland.tianyan.commons.webcore.filter;


import com.alibaba.fastjson.JSON;
import com.newland.tianya.commons.base.constants.GlobalArgumentErrorEnums;
import com.newland.tianya.commons.base.constants.GlobalTraceConstant;
import com.newland.tianya.commons.base.utils.JsonUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.skywalking.apm.toolkit.trace.TraceContext;
import org.springframework.core.MethodParameter;
import org.springframework.http.MediaType;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.servlet.mvc.method.annotation.ResponseBodyAdvice;

import java.util.HashMap;
import java.util.Map;


/**
 * @author: RojiaHuang
 * @description:
 * @date: 2021/2/25
 */
@ControllerAdvice
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
                GlobalArgumentErrorEnums errorEnums = null;
                if ("用户名或密码错误".equals(((Exception) o).getMessage())) {
                    errorEnums = GlobalArgumentErrorEnums.CLIENT_SECRET_ERROR;
                }
                if (((Exception) o).getMessage().contains("Unauthorized grant type") || ((Exception) o).getMessage().contains("Unsupported grant type")) {
                    errorEnums = GlobalArgumentErrorEnums.GRANT_TYPE_INVALID;
                }

                Map<String, Object> map = new HashMap<>();
                map.put("trace_id", traceId);
                if (errorEnums != null) {
                    map.put("error_code", errorEnums.getErrorCode());
                    map.put("error_msg", errorEnums.getErrorMsg());
                } else {
                    map.put("error_code", "6000");
                    map.put("error_msg", "system error");
                }

                return JSON.toJSON(map);
            }
        } else {
            Map<String, Object> map = JsonUtils.toMap(o);
            if (map.containsKey("image")) {
                map.remove("image");
                map.put("image", "(base转码图片，省略不打印)");
            }
            log.info("responseParams：{}", JsonUtils.toJson(map));
        }

        return o;
    }
}
