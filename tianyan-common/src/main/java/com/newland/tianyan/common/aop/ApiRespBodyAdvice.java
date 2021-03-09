package com.newland.tianyan.common.aop;

import com.alibaba.fastjson.JSON;
import com.newland.tianyan.common.constans.GlobalAuthErrorEnums;
import com.newland.tianyan.common.constans.GlobalSystemErrorEnums;
import com.newland.tianyan.common.utils.JsonUtils;
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
        String traceId = TraceContext.traceId();
        serverHttpResponse.getHeaders().add(GATEWAY_TRACE_HEAD, traceId);
        if (!mediaType.equals(MediaType.APPLICATION_JSON) || o instanceof Exception) {
            log.info("responseParams：{}", JsonUtils.toJson(o));

            if (o instanceof Exception) {
                GlobalAuthErrorEnums errorEnums = null;
                if ("用户名或密码错误".equals(((Exception) o).getMessage())) {
                    errorEnums = GlobalAuthErrorEnums.CLIENT_SECRET_ERROR;
                }
                if (((Exception) o).getMessage().contains("Unauthorized grant type") || ((Exception) o).getMessage().contains("Unsupported grant type")) {
                    errorEnums = GlobalAuthErrorEnums.GRANT_TYPE_INVALID;
                }

                Map<String, Object> map = new HashMap<>();
                map.put("trace_id", traceId);
                if (errorEnums != null) {
                    map.put("error_code", errorEnums.getErrorCode());
                    map.put("error_msg", errorEnums.getErrorMsg());
                } else {
                    map.put("error_code", GlobalSystemErrorEnums.SYSTEM_ERROR.getErrorCode());
                    map.put("error_msg", GlobalSystemErrorEnums.SYSTEM_ERROR.getErrorMsg());
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
