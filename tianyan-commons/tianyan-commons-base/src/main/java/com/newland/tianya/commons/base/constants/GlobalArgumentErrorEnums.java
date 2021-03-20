package com.newland.tianya.commons.base.constants;


import com.newland.tianya.commons.base.exception.ArgumentException;

/**
 * @author: RojiaHuang
 * @description:
 * @date: 2021/3/3
 */
public enum GlobalArgumentErrorEnums {
    /**
     * 请求方法错误
     */
    INVALID_METHOD(6301, "invalid method [{0}]"),
    /**
     * 请求方法不支持(版本错误)
     */
    SERVICE_NOT_SUPPORT(6302, "service not support"),
    /**
     * 请求参数不是json
     */
    JSON_CONTENT_FORMAT_ERROR(6303, "request body should be json format"),
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
     */
    IMAGE_SIZE_OVER_2MB(6313, "image size error（2MB）"),
    /**
     * 图 ⽚ 为 空 或 base64 解码错误
     */
    BASE64_FORMAT_ILLEGAL(6314, "empty image or not base64 encode"),
    /**
     * 暂不支持JPG、PNG、BMP以外的图片格式
     */
    IMAGE_FORMAT_ILLEGAL(6315, "image file format error"),

    /**
     * auth校验异常
     */
    NO_TOKEN(6100, "No Access token"),
    TOKEN_INVALID(6101, "Access token invalid"),
    TOKEN_EXPIRED(6102, "Access token expired"),
    CLIENT_SECRET_ERROR(6103, "client_secret error"),
    GRANT_TYPE_INVALID(6104, "grant_type invalid"),
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
