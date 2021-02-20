package com.newland.tianyan.common.model.exception;

/**
 * @program: newland-cloud
 * @description:
 * @author: THE KING
 **/
public class ErrorImageSizeException extends RuntimeException {

    private int errorCode;
    private String errorMsg;

    public ErrorImageSizeException() {
        this.errorCode = 6202;
        this.errorMsg = "image size error";
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