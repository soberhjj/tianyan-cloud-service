package com.newland.tianyan.face.constant;

import com.newland.tianyan.common.exception.SysException;

/**
 * @author: RojiaHuang
 * @description:
 * @date: 2021/3/3
 */
public enum SysErrorEnums {
    /**
     * 识别服务异常
     * */
    FACE_BACK_ERROR(5200, "后端识别服务异常"),
    /**
     * db验证
     */
    DB_INSERT_ERROR(5201, "DBMS新增失败[{0}]"),
    DB_UPDATE_ERROR(5202, "DBMS更新失败[{0}]"),
    DB_DELETE_ERROR(5203, "DBMS删除失败[{0}]"),
    SQL_NOT_VALID(5204, "SQL语句错误[{0}:{1}]"),
    /**
     * 向量集合删除失败
     **/
    VECTOR_DROP_ERROR(5205, ""),
    VECTOR_CREATE_ERROR(5206,""),
    VECTOR_INSERT_ERROR(5207,""),
    VECTOR_DELETE_ERROR(5208,""),
    VECTOR_QUERY_ERROR(5209,""),
    /**
     * proto异常
     */
    PROTO_PARSE_ERROR(5210, "proto转换失败"),

    RABBIT_MQ_RETURN_NONE(5211, "消息队列结果为空"),
    /**
     * 其他系统异常
     */
    SYSTEM_ERROR(5299, "系统异常"),
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