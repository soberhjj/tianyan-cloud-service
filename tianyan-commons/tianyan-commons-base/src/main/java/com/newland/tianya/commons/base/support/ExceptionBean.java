package com.newland.tianya.commons.base.support;

import lombok.Builder;
import lombok.Data;

/**
 * @author: RojiaHuang
 * @description:
 * @date: 2021/3/22
 */
@Data
@Builder
public class ExceptionBean {
    private int errorCode;

    private String errorMsg;

    private Object[] args;

    private Throwable throwable;

    public ExceptionBean(int errorCode, String errorMsg) {
        this.errorCode = errorCode;
        this.errorMsg = errorMsg;
    }

    public ExceptionBean(int errorCode, String errorMsg, Object[] args) {
        this.errorCode = errorCode;
        this.errorMsg = errorMsg;
        this.args = args;
    }

    public ExceptionBean(int errorCode, String errorMsg, Throwable throwable) {
        this.errorCode = errorCode;
        this.errorMsg = errorMsg;
        this.throwable = throwable;
    }

    public ExceptionBean(int errorCode, String errorMsg, Object[] args, Throwable throwable) {
        this.errorCode = errorCode;
        this.errorMsg = errorMsg;
        this.args = args;
        this.throwable = throwable;
    }
}
