package com.newland.tianya.commons.base.support;

import com.newland.tianya.commons.base.constants.GlobalExceptionEnum;
import com.newland.tianya.commons.base.exception.BaseException;
import com.newland.tianya.commons.base.model.JsonErrorObject;
import com.newland.tianya.commons.base.utils.JsonUtils;
import com.newland.tianya.commons.base.utils.LogIdUtils;

/**
 * 统一封装错误信息类
 *
 * @author: RojiaHuang
 * @description:
 * @date: 2021/3/22
 */
public class ResponseBodyConvert {
    public static String toJsonString(BaseException exception) {
        return JsonUtils.toJson(convertToJsonErrorObject(exception));
    }

    public static Object toObject(BaseException exception) {
        return JsonUtils.toObject(convertToJsonErrorObject(exception));
    }

    public static String toSnakeCaseJsonString(BaseException exception) {
        return JsonUtils.toSnakeCaseJsonString(convertToJsonErrorObject(exception));
    }

    public static Object toSnakeCaseObject(BaseException exception) {
        return JsonUtils.toSnakeCaseObject(convertToJsonErrorObject(exception));
    }

    private static JsonErrorObject convertToJsonErrorObject(BaseException exception) {
        return JsonErrorObject.builder()
                .logId(LogIdUtils.traceId())
                .errorCode(exception.getErrorCode())
                .errorMsg(exception.getErrorMsg())
                .build();
    }

    public static void main(String[] args) {
        String result = ResponseBodyConvert.toSnakeCaseJsonString(ExceptionSupport.toException(GlobalExceptionEnum.ARGUMENT_FORMAT_ERROR, "account"));
        System.out.println(result);
        String result1 = ResponseBodyConvert.toJsonString(ExceptionSupport.toException(GlobalExceptionEnum.ARGUMENT_FORMAT_ERROR,"account"));
        System.out.println(result1);
    }
}
