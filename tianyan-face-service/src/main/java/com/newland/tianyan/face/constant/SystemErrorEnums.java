package com.newland.tianyan.face.constant;


import com.newland.tianya.commons.base.exception.SysException;

/**
 * @author: RojiaHuang
 * @description:
 * @date: 2021/3/3
 */
public enum SystemErrorEnums {
    /**
     * 人脸算法服务异常
     */
    RABBIT_MQ_RETURN_NONE(6001, "backend error"),
    /**
     * db验证
     */
    DB_INSERT_ERROR(6002, "DBMS insert fail [{0}]"),
    DB_UPDATE_ERROR(6003, "DBMS update fail [{0}]"),
    DB_DELETE_ERROR(6004, "DBMS delete fail[{0}]"),
    SQL_NOT_VALID(6005, "SQL fail [{0}:{1}]"),
    /**
     * 向量集合删除失败
     **/
    VECTOR_DROP_ERROR(6006, "vector drop fail"),
    VECTOR_CREATE_ERROR(6007, "vector create collection fail"),
    VECTOR_INSERT_ERROR(6008, "vector insert fail"),
    VECTOR_DELETE_ERROR(6009, "vector delete fail"),
    VECTOR_QUERY_ERROR(6010, "vector query fail"),
    /**
     * proto异常
     */
    PROTO_PARSE_ERROR(6011, "proto转换失败"),
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
