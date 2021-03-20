package com.newland.tianya.commons.base.exception;

/**
 * @author: RojiaHuang
 * @description: 参数异常
 * @date: 2021/3/3
 */
public class ArgumentException extends BaseException {

    public ArgumentException(int errorCode, String errorMsg) {
        super(errorCode, errorMsg);
    }

    public ArgumentException(Integer errorCode, String errorMsg, Throwable e) {
        super(errorCode, errorMsg, e);
    }

    public ArgumentException(Integer errorCode, String errorMsg, Object... args) {
        super(errorCode, errorMsg, args);
    }

    public static ArgumentException create(int errorCode, String errorMsg, Object... args) {
        return new ArgumentException(errorCode, errorMsg, args);
    }

}