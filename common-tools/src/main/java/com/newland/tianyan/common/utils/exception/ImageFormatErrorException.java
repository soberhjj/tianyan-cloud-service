package com.newland.tianyan.common.utils.exception;

/**
 * @program: newland-cloud
 * @description:
 * @author: THE KING
 **/
public class ImageFormatErrorException extends RuntimeException {

    private int errorCode;
    private String errorMsg;

    public ImageFormatErrorException() {
        this.errorCode = 6201;
        this.errorMsg = "image format error";
    }

    public int getErrorCode() {
        return errorCode;
    }

    public void setErrorCode(int errorCode) {
        this.errorCode = errorCode;
    }

    public String getErrorMsg() {
        return errorMsg;
    }

    public void setErrorMsg(String errorMsg) {
        this.errorMsg = errorMsg;
    }
}
