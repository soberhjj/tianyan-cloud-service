package com.newland.tianya.commons.base.exception;

/**
 * @author: RojiaHuang
 * @description: 系统异常
 * @date: 2021/3/3
 */
public class SysException extends BaseException {

    public SysException(int errorCode, String errorMsg) {
        super(errorCode, errorMsg);
    }

    public SysException(Integer errorCode, String errorMsg, Throwable e) {
        super(errorCode, errorMsg, e);
    }

    public SysException(Integer errorCode, String errorMsg, Object... args) {
        super(errorCode, errorMsg, args);
    }

}
