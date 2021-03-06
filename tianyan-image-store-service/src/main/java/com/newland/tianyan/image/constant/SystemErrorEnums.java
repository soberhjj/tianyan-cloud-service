package com.newland.tianyan.image.constant;

import com.newland.tianyan.common.exception.SysException;

/**
 * @author: RojiaHuang
 * @description: 系统异常
 * @date: 2021/3/5
 */
public enum SystemErrorEnums {
    ;
    private int errorCode;

    private String errorMsg;

    public int getErrorCode() {
        return errorCode;
    }

    public String getErrorMsg() {
        return errorMsg;
    }

    SystemErrorEnums(int errorCode, String errorMsg) {
        this.errorCode = errorCode;
        this.errorMsg = errorMsg;
    }

    public SysException toException(Exception e) {
        return new SysException(this.getErrorCode(), this.getErrorMsg(), e);
    }

    public SysException toException(Exception e, Object... args) {
        return new SysException(this.getErrorCode(), this.getErrorMsg(), e, args);
    }
}
