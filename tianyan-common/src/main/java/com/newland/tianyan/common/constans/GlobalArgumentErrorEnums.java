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
    JSON_CONTENT_FORMAT_ERROR(6303,"request body should be json format"),
    /**
     * 参数校验异常
     */
    ARGUMENT_NOT_NULL(6304, "param [{0}] is null"),
    ARGUMENT_SIZE_MIN(6305, "param [{0}] is too short"),
    ARGUMENT_SIZE_MAX(6306, "param [{0}] is too long"),
    ARGUMENT_NOT_BLANK(6307, "param [{0}] should not be blank"),
    ARGUMENT_NOT_EMPTY(6308, "param [{0}] should not be empty"),
    ARGUMENT_SIZE(6309, "param [{0}] wrong size error"),
    ARGUMENT_FORMAT_ERROR(6310, "param [{0}] format error"),
    ARGUMENT_PATTERN(6310, "param [{0}] format error"),

    /**
     * 图片校验异常
     * */
    /**
     * 图⽚⼤⼩错误
     * */
    IMAGE_SIZE_OVER_2MB(6313,"image size error（2MB）"),
    /**
     * 图 ⽚ 为 空 或 base64 解码错误
     * */
    BASE64_FORMAT_ILLEGAL(6314,"empty image or not base64 encode"),
    /**
     * 暂不支持JPG、PNG、BMP以外的图片格式
     * */
    IMAGE_FORMAT_ILLEGAL(6315,"image file format error"),
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
