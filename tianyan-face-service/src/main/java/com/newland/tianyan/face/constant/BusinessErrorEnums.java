package com.newland.tianyan.face.constant;

import com.newland.tianyan.common.exception.BusinessException;

/**
 * @author: RojiaHuang
 * @description: 业务异常
 * @date: 2021/3/3
 */
public enum BusinessErrorEnums {
    /**
     * 存在性异常
     */
    APP_NOT_FOUND(5250, "app[{0}] not found"),
    GROUP_NOT_FOUND(5251, "group[{0}] not found"),
    USER_NOT_FOUND(5252, "user[{0}] not found"),
    FACE_NOT_FOUND(5253, "face not found"),
    APP_ALREADY_EXISTS(5254, "app[{0}] already exists"),
    GROUP_ALREADY_EXISTS(5255, "group[{0}] already exists"),
    USER_ALREADY_EXISTS(5256, "user[{0}] already exists"),

    /**
     * 业务字段验证
     */
    WRONG_ACTION_TYPE(5257, "param [action_type] format error"),
    WRONG_FACE_FIELD(5258, "param [face_field] format error"),
    OVER_USE_MAX_NUMBER(6409, "request add user over limit"),
    OVER_FACE_MAX_NUMBER(6407, "the number of user's faces is beyond the limit"),
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
