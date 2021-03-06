package com.newland.tianyan.face.constant;

import com.newland.tianyan.common.exception.BusinessException;

/**
 * @author: RojiaHuang
 * @description: 业务异常
 * @date: 2021/3/3
 */
public enum BusinessErrorEnums {
    /**
     * 测试微服务异常
     */
    DEMO(1, "demo exception"),

    /**
     * 存在性验证
     */
    APP_NOT_FOUND(1000, "app[{0}] not found"),
    GROUP_NOT_FOUND(1000,"group[{0}] not found"),
    USER_NOT_FOUND(1000,"user[{0}] not found"),
    APP_ALREADY_EXISTS(1000, "app[{0}] already exists"),
    GROUP_ALREADY_EXISTS(1000,"group[{0}] already exists"),
    USER_ALREADY_EXISTS(1000,"user[{0}] already exists"),

    /**
     * 业务字段验证
     */
    WRONG_ACTION_TYPE(1000, "param [action_type] format error"),
    WRONG_FACE_FIELD(1000, "param [face_field] format error"),
    /**
     * 业务处理结果提示
     */
    FACE_NOT_FOUND(1000, "face not found"),
    ;
    private int errorCode;

    private String errorMsg;

    public int getErrorCode() {
        return errorCode;
    }

    public String getErrorMsg() {
        return errorMsg;
    }

    BusinessErrorEnums(int errorCode, String errorMsg) {
        this.errorCode = errorCode;
        this.errorMsg = errorMsg;
    }

    public BusinessException toException() {
        return new BusinessException(this.getErrorCode(), this.getErrorMsg());
    }


    public BusinessException toException(Object... args) {
        return new BusinessException(this.getErrorCode(), this.getErrorMsg(), args);
    }

}
