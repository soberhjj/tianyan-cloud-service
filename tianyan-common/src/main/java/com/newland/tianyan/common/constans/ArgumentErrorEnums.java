package com.newland.tianyan.common.constans;

import com.newland.tianyan.common.exception.ArgumentException;

/**
 * @author: RojiaHuang
 * @description:
 * @date: 2021/3/3
 */
public enum ArgumentErrorEnums {
    /**
     * 参数校验异常
     */
    ARGUMENT_FORMAT_ERROR(6300, "Parameter [{0}] format error"),
    ARGUMENT_NOT_BLANK(6301, "Parameter [{0}] should not be blank"),
    ARGUMENT_NOT_EMPTY(6302, "Parameter [{0}] should not be empty"),
    ARGUMENT_NOT_NULL(6303, "param [{0}] should not be null"),
    ARGUMENT_SIZE_MIN(6304, "param [{0}] is too large"),
    ARGUMENT_SIZE_MAX(6305, "param [{0}] is too small"),
    ARGUMENT_SIZE(6306, "Parameter [{0}] wrong size error"),
    ARGUMENT_PATTERN(6307, "Parameter [{0}] format error"),

    /**
     * 图片校验异常
     * */
    BASE64_FORMAT_ILLEGAL(202100,"非BASE64编码串"),
    IMAGE_SIZE_OVER_2MB(202101,"图片大小超过2MB"),
    IMAGE_FORMAT_ILLEGAL(202102,"暂不支持JPG、PNG、BMP以外的图片格式"),
    ;
    private int errorCode;

    private String errorMsg;

    public int getErrorCode() {
        return errorCode;
    }

    public String getErrorMsg() {
        return errorMsg;
    }

    ArgumentErrorEnums(int errorCode, String errorMsg) {
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
