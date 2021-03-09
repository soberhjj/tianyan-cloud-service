package com.newland.tianyan.common.constans;

import com.newland.tianyan.common.exception.SysException;

/**
 * @author: RojiaHuang
 * @description:
 * @date: 2021/3/6
 */
public enum GlobalAuthErrorEnums {
    /**
     * 客户端密码错误
     */
    CLIENT_SECRET_ERROR(6103, "client_secret error"),
    /**
     * 授权类型错误或不支持
     */
    GRANT_TYPE_INVALID(6104, "grant_type invalid"),
    ;
    private int errorCode;

    private String errorMsg;

    public int getErrorCode() {
        return errorCode;
    }

    public String getErrorMsg() {
        return errorMsg;
    }

    GlobalAuthErrorEnums(int errorCode, String errorMsg) {
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
