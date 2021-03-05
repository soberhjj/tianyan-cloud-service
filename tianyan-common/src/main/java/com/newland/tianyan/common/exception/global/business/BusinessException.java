package com.newland.tianyan.common.exception.global.business;

import com.newland.tianyan.common.exception.CommonException;

/**
 * @author: RojiaHuang
 * @description: 业务异常
 * @date: 2021/3/3
 */
public class BusinessException extends CommonException {

    public BusinessException(int errorCode, String errorMsg) {
        super(errorCode, errorMsg);
    }

    public BusinessException(Integer errorCode, String errorMsg, Throwable e) {
        super(errorCode, errorMsg, e);
    }

    public BusinessException(Integer errorCode, String errorMsg, Object... args) {
        super(errorCode, errorMsg, args);
    }
}
