package com.newland.tianyan.face.constant;


import com.newland.tianya.commons.base.exception.BusinessException;

/**
 * @author: RojiaHuang
 * @description: 业务异常
 * @date: 2021/3/3
 */
public enum BusinessErrorEnums {
    /**
     * app
     */
    APP_NOT_FOUND(6401, "app[{0}] is not found"),
    APP_ALREADY_EXISTS(6402, "app[{0}] already exists"),
    /**
     * group
     */
    GROUP_NOT_FOUND(6403, "group[{0}] is not found"),
    GROUP_ALREADY_EXISTS(6404, "group[{0}] already exists"),
    EMPTY_GROUP(6405, "no user in group[{0}]"),
    /**
     * user
     */
    USER_NOT_FOUND(6406, "user[{0}] is not found"),
    USER_ALREADY_EXISTS(6407, "user[{0}] already exists"),
    //(单个group上线最大5万用户)
    OVER_USE_MAX_NUMBER(6408, "request add user over limit"),
    /**
     * face
     */
    FACE_NOT_FOUND(6409, "face is not found"),
    OVER_FACE_MAX_NUMBER(6410, "the number of user's faces is beyond the limit"),
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
