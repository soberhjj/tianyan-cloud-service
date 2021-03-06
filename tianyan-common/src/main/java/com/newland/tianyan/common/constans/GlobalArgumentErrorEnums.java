package com.newland.tianyan.common.constans;

import com.newland.tianyan.common.exception.ArgumentException;

/**
 * @author: RojiaHuang
 * @description:
 * @date: 2021/3/3
 */
public enum GlobalArgumentErrorEnums {
    /**
     * 请求参数不是json
     * */
    JSON_CONTENT_FORMAT_ERROR(2100,"request body should be json format"),
    /**
     * 参数校验异常
     */
    ARGUMENT_FORMAT_ERROR(2200, "Parameter [{0}] format error"),
    ARGUMENT_NOT_BLANK(2201, "Parameter [{0}] should not be blank"),
    ARGUMENT_NOT_EMPTY(2202, "Parameter [{0}] should not be empty"),
    ARGUMENT_NOT_NULL(2203, "param [{0}] should not be null"),
    ARGUMENT_SIZE_MIN(2204, "param [{0}] is too large"),
    ARGUMENT_SIZE_MAX(2205, "param [{0}] is too small"),
    ARGUMENT_SIZE(2206, "Parameter [{0}] wrong size error"),
    ARGUMENT_PATTERN(2207, "Parameter [{0}] format error"),

    /**
     * 图片校验异常
     * */
    BASE64_FORMAT_ILLEGAL(2300,"非BASE64编码串"),
    IMAGE_SIZE_OVER_2MB(2301,"图片大小超过2MB"),
    IMAGE_FORMAT_ILLEGAL(2302,"暂不支持JPG、PNG、BMP以外的图片格式"),
    ;
    private int errorCode;

    private String errorMsg;

    public int getErrorCode() {
        return errorCode;
    }

    public String getErrorMsg() {
        return errorMsg;
    }

    GlobalArgumentErrorEnums(int errorCode, String errorMsg) {
        this.errorCode = errorCode;
        this.errorMsg = errorMsg;
    }

    public ArgumentException toException() {
        return new ArgumentException(this.getErrorCode(), this.getErrorMsg());
    }

    public ArgumentException toException(Object... args) {
        return new ArgumentException(this.getErrorCode(), this.getErrorMsg(), args);
    }

}
