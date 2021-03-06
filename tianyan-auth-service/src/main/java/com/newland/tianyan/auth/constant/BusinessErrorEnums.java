package com.newland.tianyan.auth.constant;

import com.newland.tianyan.common.exception.BusinessException;

/**
 * @author: RojiaHuang
 * @description: 业务异常
 * @date: 2021/3/3
 */
public enum BusinessErrorEnums {
    /**
     * 存在性验证
     * */
    ACCOUNT_NOT_FOUND(5150,"account[{0}] not found"),
    MAIL_BOX_NOT_FOUND(5151,"mailbox[{0}] not found"),
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
