package com.newland.tianyan.common.exception;

import lombok.Data;

import java.text.MessageFormat;

@Data
public class CommonException extends RuntimeException{
    private int errorCode;
    private String errorMsg;
    private Object[] args;

    public CommonException(int errorCode, String errorMsg) {
        this.errorCode = errorCode;
        this.errorMsg = errorMsg;
    }

    public CommonException(int errorCode, String errorMsg, Object... args) {
        this.errorCode = errorCode;
        this.errorMsg = formatMsg(errorMsg, args);
        this.args = args;
    }

    protected static String formatMsg(String str, Object... args) {
        return args != null && args.length != 0 ? MessageFormat.format(str, args) : str;
    }
}
