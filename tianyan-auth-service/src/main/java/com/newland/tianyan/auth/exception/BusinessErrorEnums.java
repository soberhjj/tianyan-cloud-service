package com.newland.tianyan.auth.exception;

import com.newland.tianyan.common.exception.global.business.BusinessException;

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
     * */
    NOT_EXISTS(101001, "[{0}]不存在"),
    ALREADY_EXISTS(101002, "[{0}]已存在"),
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
