package com.newland.tianyan.common.constans;

import com.newland.tianyan.common.exception.SysException;

/**
 * @author: RojiaHuang
 * @description:
 * @date: 2021/3/6
 */
public enum GlobalSystemErrorEnums {
    /**
     * 系统异常
     * */
    SYSTEM_ERROR(9999,"system error"),
    /**
     * 404
     * */
    INVALID_METHOD(9998,"invalid method"),
    ;
    private int errorCode;

    private String errorMsg;

    public int getErrorCode() {
        return errorCode;
    }

    public String getErrorMsg() {
        return errorMsg;
    }

    GlobalSystemErrorEnums(int errorCode, String errorMsg) {
        this.errorCode = errorCode;
        this.errorMsg = errorMsg;
    }

    public SysException toException() {
        return new SysException(this.getErrorCode(), this.getErrorMsg());
    }

    public SysException toException(Object... args) {
        return new SysException(this.getErrorCode(), this.getErrorMsg(), args);
    }

    public SysException toException(Exception e) {
        return new SysException(this.getErrorCode(), this.getErrorMsg(), e);
    }

    public SysException toException(Exception e, Object... args) {
        return new SysException(this.getErrorCode(), this.getErrorMsg(), e, args);
    }
}
