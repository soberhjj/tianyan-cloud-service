package com.newland.tianya.commons.base.exception;

import lombok.Builder;
import lombok.Data;

import java.text.MessageFormat;

/**
 * @author newland
 * @description: 公共异常类
 */
@Data
@Builder
public class BaseException extends RuntimeException {

    private int errorCode;

    private String errorMsg;

    private Object[] args;

    private Throwable throwable;

    public BaseException(int errorCode, String errorMsg) {
        this.errorCode = errorCode;
        this.errorMsg = errorMsg;
    }

    public BaseException(int errorCode, String errorMsg, Object... args) {
        this.errorCode = errorCode;
        this.errorMsg = formatMsg(errorMsg, args);
        this.args = args;
    }

    public BaseException(int errorCode, String errorMsg, Throwable throwable) {
        this.errorCode = errorCode;
        this.errorMsg = errorMsg;
        this.throwable = throwable;
    }

    public BaseException(int errorCode, String errorMsg, Throwable throwable, Object... args) {
        this.errorCode = errorCode;
        this.errorMsg = formatMsg(errorMsg, args);
        this.throwable = throwable;
        this.args = args;
    }

    protected static String formatMsg(String str, Object... args) {
        return args != null && args.length != 0 ? MessageFormat.format(str, args) : str;
    }

    public static void main(String[] args) {
        String str = "param [{0}] should not be empty";
        String arg = "account";
        //String result = MessageFormat.format(str, arg);
        String result = formatMsg(str, arg);
        System.out.println(result);
    }
}
