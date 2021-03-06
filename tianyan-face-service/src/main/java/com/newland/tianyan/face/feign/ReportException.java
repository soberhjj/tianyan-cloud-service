package com.newland.tianyan.face.feign;

import com.netflix.hystrix.exception.HystrixBadRequestException;

import java.text.MessageFormat;

/**
 * @author: RojiaHuang
 * @description:
 * @date: 2021/3/5
 */
public class ReportException extends HystrixBadRequestException {
    private int errorCode;
    private String errorMsg;
    private Object[] args;

    public ReportException(int errorCode, String errorMsg) {
        super(errorMsg);
        this.errorCode = errorCode;
        this.errorMsg = errorMsg;
    }

    public ReportException(int errorCode, String errorMsg, Object... args) {
        super(errorMsg);
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
