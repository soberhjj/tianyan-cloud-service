package com.newland.tianyan.face.constant;

import com.newland.tianyan.common.exception.ArgumentException;

/**
 * @author: RojiaHuang
 * @description: 业务异常
 * @date: 2021/3/3
 */
public enum ArgumentErrorEnums {
    /**
     * 存在性异常
     */
    WRONG_FACE_FIELD(6311, "param [face_field] format error"),
    WRONG_ACTION_TYPE(6312, "param [action_type] format error"),
    ;
    private int errorCode;

    private String errorMsg;

    public int getErrorCode() {
        return errorCode;
    }

    public String getErrorMsg() {
        return errorMsg;
    }

    ArgumentErrorEnums(int errorCode, String errorMsg) {
        this.errorCode = errorCode;
        this.errorMsg = errorMsg;
    }

    public ArgumentException toException() {
        return new ArgumentException(this.getErrorCode(), this.getErrorMsg());
    }


    public ArgumentException toException(Object... args) {
        return new ArgumentException(this.getErrorCode(), this.getErrorMsg(), args);
    }

}
