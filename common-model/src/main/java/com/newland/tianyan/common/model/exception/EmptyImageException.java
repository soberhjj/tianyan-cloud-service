package com.newland.tianyan.common.model.exception;

/**
 * @program: newland-cloud
 * @description:
 * @author: THE KING
 **/
public class EmptyImageException extends RuntimeException {

    private int errorCode;
    private String errorMsg;

    public EmptyImageException() {
        this.errorCode = 6200;
        this.errorMsg = "empty image";
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

