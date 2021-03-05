package com.newland.tianyan.auth.exception;

import com.newland.tianyan.common.exception.global.system.SysException;

/**
 * @author: RojiaHuang
 * @description: 系统异常
 * @date: 2021/3/5
 */
public enum SystemErrorEnums {
    /**
     * db验证
     */
    DB_INSERT_ERROR(102001, "DBMS新增失败[{0}]"),
    DB_UPDATE_ERROR(102002, "DBMS更新失败[{0}]"),
    DB_DELETE_ERROR(102003, "DBMS删除失败[{0}]"),
    SQL_NOT_VALID(102004, "SQL语句错误[{0}:{1}]"),
    /**
     * proto异常
     */
    PROTO_PARSE_ERROR(601007, "proto转换失败"),
    /**
     * 其他系统异常
     */
    SYSTEM_ERROR(999999, "系统异常"),
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
