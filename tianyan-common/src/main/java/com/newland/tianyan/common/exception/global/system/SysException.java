package com.newland.tianyan.common.exception.global.system;

import com.newland.tianyan.common.exception.CommonException;

/**
 * @author: RojiaHuang
 * @description: 系统异常
 * @date: 2021/3/3
 */
public class SysException extends CommonException {

    public SysException(int errorCode, String errorMsg) {
        super(errorCode, errorMsg);
    }

    public SysException(Integer errorCode, String errorMsg, Throwable e) {
        super(errorCode, errorMsg, e);
    }

    public SysException(Integer errorCode, String errorMsg, Object... args) {
        super(errorCode, errorMsg, args);
    }
}
