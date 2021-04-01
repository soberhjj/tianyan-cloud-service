package com.newland.tianya.commons.base.constants;

import com.newland.tianya.commons.base.support.IExceptionEnums;

/**
 * @author: RojiaHuang
 * @description:
 * @date: 2021/3/22
 */
public enum GlobalExceptionEnum implements IExceptionEnums {
    /**
     * 请求方法错误
     */
    INVALID_METHOD(6301, "invalid method [{0}]", ExceptionTypeEnums.ARGUMENT_EXCEPTION),
    /**
     * 请求方法不支持(版本错误)
     */
    SERVICE_NOT_SUPPORT(6302, "service not support", ExceptionTypeEnums.ARGUMENT_EXCEPTION),
    /**
     * 请求参数不是json
     */
    JSON_CONTENT_FORMAT_ERROR(6303, "request body should be json format", ExceptionTypeEnums.ARGUMENT_EXCEPTION),
    /**
     * 参数校验异常
     */
    ARGUMENT_NOT_NULL(6304, "param [{0}] is null", ExceptionTypeEnums.ARGUMENT_EXCEPTION),
    ARGUMENT_SIZE_MIN(6305, "param [{0}] is too short", ExceptionTypeEnums.ARGUMENT_EXCEPTION),
    ARGUMENT_SIZE_MAX(6306, "param [{0}] is too long", ExceptionTypeEnums.ARGUMENT_EXCEPTION),
    ARGUMENT_NOT_BLANK(6307, "param [{0}] should not be blank", ExceptionTypeEnums.ARGUMENT_EXCEPTION),
    ARGUMENT_NOT_EMPTY(6308, "param [{0}] should not be empty", ExceptionTypeEnums.ARGUMENT_EXCEPTION),
    ARGUMENT_SIZE(6309, "param [{0}] wrong size error", ExceptionTypeEnums.ARGUMENT_EXCEPTION),
    ARGUMENT_FORMAT_ERROR(6310, "param [{0}] format error", ExceptionTypeEnums.ARGUMENT_EXCEPTION),
    ARGUMENT_PATTERN(6310, "param [{0}] format error", ExceptionTypeEnums.ARGUMENT_EXCEPTION),
    ARGUMENT_INVALID_FORMAT(6311, "param [{0}] invalid format error[{1}]", ExceptionTypeEnums.ARGUMENT_EXCEPTION),
    /**
     * 图⽚⼤⼩错误
     */
    IMAGE_SIZE_OVER_2MB(6313, "image size error（2MB）", ExceptionTypeEnums.ARGUMENT_EXCEPTION),
    /**
     * 图 ⽚ 为 空 或 base64 解码错误
     */
    BASE64_FORMAT_ILLEGAL(6314, "empty image or not base64 encode", ExceptionTypeEnums.ARGUMENT_EXCEPTION),
    /**
     * 暂不支持JPG、PNG、BMP以外的图片格式
     */
    IMAGE_FORMAT_ILLEGAL(6315, "image file format error", ExceptionTypeEnums.ARGUMENT_EXCEPTION),
    /**
     * auth校验异常
     */
    NO_TOKEN(6100, "No Access token", ExceptionTypeEnums.ARGUMENT_EXCEPTION),
    TOKEN_INVALID(6101, "Access token invalid", ExceptionTypeEnums.ARGUMENT_EXCEPTION),
    TOKEN_EXPIRED(6102, "Access token expired", ExceptionTypeEnums.ARGUMENT_EXCEPTION),
    CLIENT_SECRET_ERROR(6103, "client_secret error", ExceptionTypeEnums.ARGUMENT_EXCEPTION),
    GRANT_TYPE_INVALID(6104, "grant_type invalid", ExceptionTypeEnums.ARGUMENT_EXCEPTION),
    /**
     * 非法SQL异常
     */
    SQL_NOT_VALID(6005, "SQL fail [{0}:{1}]", ExceptionTypeEnums.SYSTEM_EXCEPTION),
    SYSTEM_ERROR(6000, "system error", ExceptionTypeEnums.SYSTEM_EXCEPTION),
    ;
    private int errorCode;

    private String errorMsg;

    private ExceptionTypeEnums typeEnums;

    GlobalExceptionEnum(int errorCode, String errorMsg, ExceptionTypeEnums typeEnums) {
        this.errorCode = errorCode;
        this.errorMsg = errorMsg;
        this.typeEnums = typeEnums;
    }

    @Override
    public int getErrorCode() {
        return this.errorCode;
    }

    @Override
    public String getErrorMsg() {
        return this.errorMsg;
    }

    @Override
    public ExceptionTypeEnums getTypeEnums() {
        return this.typeEnums;
    }
}
