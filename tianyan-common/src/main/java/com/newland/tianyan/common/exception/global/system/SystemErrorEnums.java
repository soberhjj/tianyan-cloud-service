package com.newland.tianyan.common.exception.global.system;

/**
 * @author: RojiaHuang
 * @description:
 * @date: 2021/3/3
 */
public enum SystemErrorEnums {
    /**
     * proto异常
     */
    PROTO_PARSE_ERROR(601007, "proto转换失败"),
    /**
     * db验证
     */
    DB_INSERT_ERROR(102001, "DBMS新增失败[{0}]"),
    DB_UPDATE_ERROR(102002, "DBMS更新失败[{0}]"),
    DB_DELETE_ERROR(102003, "DBMS删除失败[{0}]"),
    SQL_NOT_VALID(102004, "SQL语句错误[{0}:{1}]"),
    /**
     * 向量存储验证
     * */
    CACHE_QUERY_ERROR(103000, "cache查询失败[{0}:{1}]"),
    CACHE_INSERT_ERROR(103001, "cache新增失败[{0}:{1}]"),
    CACHE_DELETE_ERROR(103002, "cache删除失败[{0}:{1}]"),
    CACHE_CREATE_ERROR(103003, "cache结果集创建失败"),
    CACHE_DROP_ERROR(103004, "cache结果集删除失败"),
    /**
     * 消息队列验证
     * */
    RABBIT_MQ_RETURN_NONE(600100, "消息队列请求结果为空"),
    /**
     * 消息队列验证
     * */
    CHECK_VERIFY_FAIL(601001, "验证签名失败{0}"),
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
}
