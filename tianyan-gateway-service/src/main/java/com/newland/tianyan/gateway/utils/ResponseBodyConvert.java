package com.newland.tianyan.gateway.utils;

import com.newland.tianya.commons.base.constants.GlobalExceptionEnum;
import org.slf4j.MDC;

/**
 * 统一封装错误信息类
 *
 * @author: RojiaHuang
 * @description:
 * @date: 2021/3/22
 */
public class ResponseBodyConvert {

    public static String convert(GlobalExceptionEnum exceptionEnum) {
        return "{\"log_id\":" + MDC.get("traceId") + ",\"error_code\":" + exceptionEnum.getErrorCode() + ",\"error_msg\": \"" + exceptionEnum.getErrorMsg() + "\"}";
    }
}
