package com.newland.tianyan.auth.exception;

/**
 * @program: newland-cloud
 * @description:
 * @author: THE KING
 **/
public class CommonException extends Exception {
    private int errorCode;
    private String msg;

    public CommonException(int errorCode, String msg) {
        this.errorCode = errorCode;
        this.msg = msg;
    }

    public int getErrorCode() {
        return errorCode;
    }

    public void setErrorCode(int errorCode) {
        this.errorCode = errorCode;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }
}
