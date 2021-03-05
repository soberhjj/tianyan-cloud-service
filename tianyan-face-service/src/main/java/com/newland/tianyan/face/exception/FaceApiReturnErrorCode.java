//package com.newland.tianyan.face.exception;
//
///**
// * @author: RojiaHuang
// * @description: 业务错误码枚举
// * @date: 2021/1/12
// */
//public enum FaceApiReturnErrorCode {
//    /**
//     * 存在性验证
//     * */
//    NOT_EXISTS(101001, "[{0}]不存在"),
//    ALREADY_EXISTS(101002, "[{0}]已存在"),
//    NOT_ACTIVE(101003, "[{0}]状态无效"),
//    /**
//     * 向量存储验证
//     * */
//    CACHE_INSERT_ERROR(103001, "cache新增失败[{0}:{1}]"),
//    CACHE_DELETE_ERROR(103002, "cache删除失败[{0}:{1}]"),
//    CACHE_CREATE_ERROR(103003, "cache结果集创建失败"),
//    CACHE_DROP_ERROR(103004, "cache结果集删除失败"),
//    /**
//     * 消息队列验证
//     * */
//    RABBIT_MQ_RETURN_NONE(600100, "消息队列请求结果为空"),
//    /**
//     * 消息队列验证
//     * */
//    CHECK_VERIFY_FAIL(601001, "验证签名失败{0}"),
//    /**
//     * 调用验证
//     * */
//    CHECK_TIME_OUT(601002, "调用超时"),
//    CHECK_NOT_HANDLER(601003, "非法访问"),
//    ILLEGAL_ARGUMENT(601006, "非法参数[{0}]"),
//    /**
//     * 其他异常
//     * */
//    SYSTEM_ERROR(601999, "系统异常:{0}"),
//    ;
//
//    private int errorCode;
//
//    private String errorMsg;
//
//    public int getErrorCode() {
//        return errorCode;
//    }
//
//    public String getErrorMsg() {
//        return errorMsg;
//    }
//
//    FaceApiReturnErrorCode(int errorCode, String errorMsg) {
//        this.errorCode = errorCode;
//        this.errorMsg = errorMsg;
//    }
//
//    public FaceApiException toException() {
//        return new FaceApiException(this.getErrorCode(), this.getErrorMsg());
//    }
//
//    /**
//     * 封装捕获到的业务参数至异常类
//     *
//     * @param args 业务参数
//     */
//    public FaceApiException toException(Object... args) {
//        return new FaceApiException(this.getErrorCode(), this.getErrorMsg(), args);
//    }
//
//    /**
//     * 封装捕获到的exception至异常类
//     *
//     * @param e try-catch透传的exception
//     */
//    public FaceApiException toException(Exception e) {
//        return new FaceApiException(this.getErrorCode(), this.getErrorMsg(), e);
//    }
//
//    /**
//     * 封装捕获到的exception及业务参数至异常类
//     *
//     * @param e    try-catch透传的exception
//     * @param args 业务参数
//     */
//    public FaceApiException toException(Exception e, Object... args) {
//        return new FaceApiException(this.getErrorCode(), this.getErrorMsg(), e, args);
//    }
//}
