package com.newland.tianyan.face.common.exception;

public enum FaceServiceErrorEnum {
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
    ;
    private int errorCode;

    private String errorMsg;

    public int getErrorCode() {
        return errorCode;
    }

    public String getErrorMsg() {
        return errorMsg;
    }

    FaceServiceErrorEnum(int errorCode, String errorMsg) {
        this.errorCode = errorCode;
        this.errorMsg = errorMsg;
    }

    public FaceServiceException toException() {
        return new FaceServiceException(this.getErrorCode(), this.getErrorMsg());
    }

    /**
     * 封装捕获到的业务参数至异常类
     *
     * @param args 业务参数
     */
    public FaceServiceException toException(Object... args) {
        return new FaceServiceException(this.getErrorCode(), this.getErrorMsg(), args);
    }

    /**
     * 封装捕获到的exception至异常类
     *
     * @param e try-catch透传的exception
     */
    public FaceServiceException toException(Exception e) {
        return new FaceServiceException(this.getErrorCode(), this.getErrorMsg(), e);
    }

    /**
     * 封装捕获到的exception及业务参数至异常类
     *
     * @param e    try-catch透传的exception
     * @param args 业务参数
     */
    public FaceServiceException toException(Exception e, Object... args) {
        return new FaceServiceException(this.getErrorCode(), this.getErrorMsg(), e, args);
    }
}
