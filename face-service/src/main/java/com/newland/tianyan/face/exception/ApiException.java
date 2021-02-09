package com.newland.tianyan.face.exception;


import com.newland.tianyan.common.utils.exception.CommonException;

/**
 * @author: RojiaHuang
 * @description:
 * @date: 2021/1/12
 */
public class ApiException extends CommonException {

    public ApiException(int errorCode, String errorMsg) {
        super(errorCode, errorMsg);
    }

    public ApiException(Integer errorCode, String errorMsg, Throwable e) {
        super(errorCode, errorMsg, e);
    }

    public ApiException(Integer errorCode, String errorMsg, Object... args) {
        super(errorCode, errorMsg, args);
    }
}
