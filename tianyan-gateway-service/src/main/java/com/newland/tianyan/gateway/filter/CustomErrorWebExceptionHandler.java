package com.newland.tianyan.gateway.filter;

import com.newland.tianya.commons.base.constants.GlobalExceptionEnum;
import com.newland.tianya.commons.base.exception.BaseException;
import com.newland.tianya.commons.base.support.ExceptionSupport;
import com.newland.tianya.commons.base.utils.LogIdUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.web.ErrorProperties;
import org.springframework.boot.autoconfigure.web.ResourceProperties;
import org.springframework.boot.autoconfigure.web.reactive.error.DefaultErrorWebExceptionHandler;
import org.springframework.boot.web.reactive.error.ErrorAttributes;
import org.springframework.cloud.gateway.support.NotFoundException;
import org.springframework.context.ApplicationContext;
import org.springframework.http.HttpStatus;
import org.springframework.web.reactive.function.server.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.HashMap;
import java.util.Map;

/**
 * 自定义异常处理
 *
 * <p>异常时用JSON代替HTML异常信息<p>
 *
 * @author internet
 */
@Slf4j
public class CustomErrorWebExceptionHandler extends DefaultErrorWebExceptionHandler {

    public CustomErrorWebExceptionHandler(ErrorAttributes errorAttributes, ResourceProperties resourceProperties,
                                          ErrorProperties errorProperties, ApplicationContext applicationContext) {
        super(errorAttributes, resourceProperties, errorProperties, applicationContext);
    }

    /**
     * 指定响应处理方法为JSON处理的方法
     *
     * @param errorAttributes
     */
    @Override
    protected RouterFunction<ServerResponse> getRoutingFunction(ErrorAttributes errorAttributes) {
        return RouterFunctions.route(RequestPredicates.all(), this::renderErrorResponse);
    }

    /**
     * 获取异常属性
     */
    @Override
    protected Map<String, Object> getErrorAttributes(ServerRequest request, boolean includeStackTrace) {
        //默认抛出系统异常
        int code = HttpStatus.INTERNAL_SERVER_ERROR.value();
        BaseException baseException = ExceptionSupport.toException(GlobalExceptionEnum.SYSTEM_ERROR);
        Throwable error = super.getError(request);
        //微服务未注册或未被发现
        if (error instanceof org.springframework.cloud.gateway.support.NotFoundException) {
            code = HttpStatus.NOT_FOUND.value();
            baseException = ExceptionSupport.toException(GlobalExceptionEnum.SERVICE_NOT_SUPPORT,((NotFoundException) error).getReason());
        }
        //请求了未配置的URI
        if (error instanceof ResponseStatusException) {
            boolean wrongUri = "404 NOT_FOUND \"No matching handler\"".equals(error.getMessage());
            if (wrongUri) {
                code = HttpStatus.NOT_FOUND.value();
                baseException = ExceptionSupport.toException(GlobalExceptionEnum.INVALID_METHOD,request.uri().getPath());
            }
        }
        log.error(String.format("Failed to handle request [ %s ]", request.uri()), error);
        return response(code, baseException);
    }

    /**
     * 构建返回的JSON数据格式
     *
     * @param status 状态码
     * @return
     */
    private Map<String, Object> response(int status, BaseException exception) {
        Map<String, Object> response = new HashMap<>(4);
        response.put("status", status);
        response.put("log_id", LogIdUtils.traceId());
        response.put("error_code", exception.getErrorCode());
        response.put("error_msg", exception.getErrorMsg());
        return response;
    }

}
