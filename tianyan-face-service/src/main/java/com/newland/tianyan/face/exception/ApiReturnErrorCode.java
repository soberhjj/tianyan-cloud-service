package com.newland.tianyan.face.exception;

/**
 * @author: RojiaHuang
 * @description:
 * @date: 2021/1/12
 */
public enum ApiReturnErrorCode {
    NOT_EXISTS(101001, "[{0}]不存在"),
    ALREADY_EXISTS(101002, "[{0}]已存在"),
    NOT_ACTIVE(101003, "[{0}]状态无效"),

    DB_INSERT_ERROR(102001, "DBMS新增失败[{0}]"),
    DB_UPDATE_ERROR(102002, "DBMS更新失败[{0}]"),
    DB_DELETE_ERROR(102003, "DBMS删除失败[{0}]"),
    SQL_NOT_VALID(102004, "SQL语句错误[{0}:{1}]"),

    CACHE_INSERT_ERROR(103001, "cache新增失败[{0}:{1}]"),
    CACHE_DELETE_ERROR(103002, "cache删除失败[{0}:{1}]"),
    CACHE_CREATE_ERROR(103003, "cache结果集创建失败"),
    CACHE_DROP_ERROR(103004, "cache结果集删除失败"),

    RABBIT_MQ_RETURN_NONE(600100, "消息队列请求结果为空"),
    CHECK_VERIFY_FAIL(601001, "验证签名失败{0}"),
    CHECK_TIME_OUT(601002, "调用超时"),
    CHECK_NOT_HANDLER(601003, "非法访问"),
    ARGUMENT_NOT_VALID(601004, "参数校验失败[{0}:{1}]"),
    ARGUMENT_FORMAT_FAIL(601005, "参数格式异常[{0}]"),
    ILLEGAL_ARGUMENT(601006, "非法参数[{0}]"),
    SYSTEM_ERROR(601999, "系统异常:{0}"),
    ;

    private int errorCode;

    private String errorMsg;

    public int getErrorCode() {
        return errorCode;
    }

    public String getErrorMsg() {
        return errorMsg;
    }

    ApiReturnErrorCode(int errorCode, String errorMsg) {
        this.errorCode = errorCode;
        this.errorMsg = errorMsg;
    }

    public ApiException toException() {
        return new ApiException(this.getErrorCode(), this.getErrorMsg());
    }

    /**
     * 封装捕获到的业务参数至异常类
     *
     * @param args 业务参数
     */
    public ApiException toException(Object... args) {
        return new ApiException(this.getErrorCode(), this.getErrorMsg(), args);
    }

    /**
     * 封装捕获到的exception至异常类
     *
     * @param e try-catch透传的exception
     */
    public ApiException toException(Exception e) {
        return new ApiException(this.getErrorCode(), this.getErrorMsg(), e);
    }

    /**
     * 封装捕获到的exception及业务参数至异常类
     *
     * @param e    try-catch透传的exception
     * @param args 业务参数
     */
    public ApiException toException(Exception e, Object... args) {
        return new ApiException(this.getErrorCode(), this.getErrorMsg(), e, args);
    }
}
