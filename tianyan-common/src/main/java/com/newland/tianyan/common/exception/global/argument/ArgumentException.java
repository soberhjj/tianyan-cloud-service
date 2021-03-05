package com.newland.tianyan.common.exception.global.argument;

import com.newland.tianyan.common.exception.CommonException;

/**
 * @author: RojiaHuang
 * @description: 参数异常
 * @date: 2021/3/3
 */
public class ArgumentException extends CommonException {

    public ArgumentException(int errorCode, String errorMsg) {
        super(errorCode, errorMsg);
    }

    public ArgumentException(Integer errorCode, String errorMsg, Throwable e) {
        super(errorCode, errorMsg, e);
    }

    public ArgumentException(Integer errorCode, String errorMsg, Object... args) {
        super(errorCode, errorMsg, args);
    }
}