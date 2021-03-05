package com.newland.tianyan.common.exception;

import java.text.MessageFormat;

/**
 * @description: 公共异常类
 * @author newland
 */
public class BaseException extends RuntimeException {

    private int errorCode;
    private String errorMsg;
    private Object[] args;

    public BaseException(int errorCode, String errorMsg) {
        this.errorCode = errorCode;
        this.errorMsg = errorMsg;
    }

    public BaseException(int errorCode, String errorMsg, Object... args) {
        this.errorCode = errorCode;
        this.errorMsg = formatMsg(errorMsg, args);
        this.args = args;
    }

    protected static String formatMsg(String str, Object... args) {
        return args != null && args.length != 0 ? MessageFormat.format(str, args) : str;
    }

    public Object[] getArgs() {
        return args;
    }

    public void setArgs(Object[] args) {
        this.args = args;
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
