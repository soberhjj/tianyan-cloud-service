package com.newland.tianyan.common.constans;

import com.newland.tianyan.common.exception.ArgumentException;

/**
 * @author: RojiaHuang
 * @description:
 * @date: 2021/3/3
 */
public enum ArgumentErrorEnums {
    /**
     * 参数校验异常
     */
    ARGUMENT_NOT_VALID(601000, "Parameter [{0}] verification exception"),
    ARGUMENT_NOT_NULL(601001, "Parameter [{0}] not null exception"),
    ARGUMENT_NOT_BLANK(601002, "Parameter [{0}] not blank exception"),
    ARGUMENT_NOT_EMPTY(601003, "Parameter [{0}] not empty exception"),
    ARGUMENT_SIZE_MIN(601004, "Parameter [{0}] below min exception"),
    ARGUMENT_SIZE_MAN(601005, "Parameter [{0}] over max exception"),
    ARGUMENT_SIZE(601006, "Parameter [{0}] wrong size exception"),
    ARGUMENT_PATTERN(601007, "Parameter [{0}] wrong pattern exception"),
    /**
     * 参数格式异常
     */
    ARGUMENT_FORMAT_FAIL(602000, "Parameter format exception"),
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
