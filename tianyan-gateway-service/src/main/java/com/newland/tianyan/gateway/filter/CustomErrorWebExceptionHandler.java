package com.newland.tianyan.gateway.filter;

import java.util.HashMap;
import java.util.Map;

import org.springframework.boot.autoconfigure.web.ErrorProperties;
import org.springframework.boot.autoconfigure.web.ResourceProperties;
import org.springframework.boot.autoconfigure.web.reactive.error.DefaultErrorWebExceptionHandler;
import org.springframework.boot.web.reactive.error.ErrorAttributes;
import org.springframework.context.ApplicationContext;
import org.springframework.http.HttpStatus;
import org.springframework.web.reactive.function.server.RequestPredicates;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.RouterFunctions;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;

import com.newland.tianya.commons.base.constants.GlobalExceptionEnum;
import com.newland.tianya.commons.base.utils.LogIdUtils;

import lombok.extern.slf4j.Slf4j;

/**
 * 自定义异常处理
 * 
 * <p>异常时用JSON代替HTML异常信息<p>
 * 
 * @author internet
 *
 */
@Slf4j
public class CustomErrorWebExceptionHandler extends DefaultErrorWebExceptionHandler {
	
	public CustomErrorWebExceptionHandler(ErrorAttributes errorAttributes, ResourceProperties resourceProperties,
			ErrorProperties errorProperties, ApplicationContext applicationContext) {
		super(errorAttributes, resourceProperties, errorProperties, applicationContext);
	}

	/**
	 * 指定响应处理方法为JSON处理的方法
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
		int code = HttpStatus.INTERNAL_SERVER_ERROR.value();
		Throwable error = super.getError(request);
		if (error instanceof org.springframework.cloud.gateway.support.NotFoundException) {
			code =  HttpStatus.NOT_FOUND.value();
		}
		log.error(String.format("Failed to handle request [ %s ]", request.uri()), error);
		return response(code);
	}

	/**
	 * 构建返回的JSON数据格式
	 * @param status		状态码
	 * @return
	 */
	private Map<String, Object> response(int status) {
		Map<String, Object> response = new HashMap<>();
		response.put("status", status);
		response.put("log_id", LogIdUtils.traceId());
		response.put("error_code", GlobalExceptionEnum.SYSTEM_ERROR.getErrorCode());
		response.put("error_msg", GlobalExceptionEnum.SYSTEM_ERROR.getErrorMsg());
		return response;
	}

}
