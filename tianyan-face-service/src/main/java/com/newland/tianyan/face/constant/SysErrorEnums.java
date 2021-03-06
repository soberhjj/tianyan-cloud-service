package com.newland.tianyan.face.constant;

import com.newland.tianyan.common.exception.SysException;

/**
 * @author: RojiaHuang
 * @description:
 * @date: 2021/3/3
 */
public enum SysErrorEnums {
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

    RABBIT_MQ_RETURN_NONE(6011111, "消息队列结果为空"),
    /**
     * 其他系统异常
     */
    SYSTEM_ERROR(999999, "系统异常"),
    /**
     * 向量集合删除失败
     **/
    VECTOR_DROP_ERROR(100000, ""),
    VECTOR_CREATE_ERROR(100000,""),
    VECTOR_INSERT_ERROR(100000,""),
    VECTOR_DELETE_ERROR(100000,""),
    VECTOR_QUERY_ERROR(100000,""),
    ;
    private int errorCode;

    private String errorMsg;

    public int getErrorCode() {
        return errorCode;
    }

    public String getErrorMsg() {
        return errorMsg;
    }

    SysErrorEnums(int errorCode, String errorMsg) {
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
