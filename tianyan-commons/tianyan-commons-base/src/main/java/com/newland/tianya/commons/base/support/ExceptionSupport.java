package com.newland.tianya.commons.base.support;

import com.newland.tianya.commons.base.constants.ExceptionTypeEnums;
import com.newland.tianya.commons.base.exception.BaseException;
import com.newland.tianya.commons.base.exception.BusinessException;
import com.newland.tianya.commons.base.exception.SysException;

/**
 * @author: RojiaHuang
 * @description:
 * @date: 2021/3/20
 */
public class ExceptionSupport {

    public static BaseException toException(ExceptionTypeEnums exceptionTypeEnums, Integer code, String message) {
        if (ExceptionTypeEnums.ARGUMENT_EXCEPTION.equals(exceptionTypeEnums)) {
            return new BaseException(code, message);
        }
        if (ExceptionTypeEnums.SYSTEM_EXCEPTION.equals(exceptionTypeEnums)) {
            return new SysException(code, message);
        }
        if (ExceptionTypeEnums.BUSINESS_EXCEPTION.equals(exceptionTypeEnums)) {
            return new BusinessException(code, message);
        }
        return null;
    }

    public static BaseException toException(ExceptionTypeEnums exceptionTypeEnums, Integer code, String message, Object... args) {
        if (ExceptionTypeEnums.ARGUMENT_EXCEPTION.equals(exceptionTypeEnums)) {
            return new BaseException(code, message, args);
        }
        if (ExceptionTypeEnums.SYSTEM_EXCEPTION.equals(exceptionTypeEnums)) {
            return new SysException(code, message, args);
        }
        if (ExceptionTypeEnums.BUSINESS_EXCEPTION.equals(exceptionTypeEnums)) {
            return new BusinessException(code, message, args);
        }
        return null;
    }

    public static BaseException toException(ExceptionTypeEnums exceptionTypeEnums, Integer code, String message, Throwable throwable) {
        if (ExceptionTypeEnums.ARGUMENT_EXCEPTION.equals(exceptionTypeEnums)) {
            return new BaseException(code, message, throwable);
        }
        if (ExceptionTypeEnums.SYSTEM_EXCEPTION.equals(exceptionTypeEnums)) {
            return new SysException(code, message, throwable);
        }
        if (ExceptionTypeEnums.BUSINESS_EXCEPTION.equals(exceptionTypeEnums)) {
            return new BusinessException(code, message, throwable);
        }
        return null;
    }

    public static BaseException toException(ExceptionTypeEnums exceptionTypeEnums, Integer code, String message, Throwable throwable, Object... args) {
        if (ExceptionTypeEnums.ARGUMENT_EXCEPTION.equals(exceptionTypeEnums)) {
            return new BaseException(code, message, throwable, args);
        }
        if (ExceptionTypeEnums.SYSTEM_EXCEPTION.equals(exceptionTypeEnums)) {
            return new SysException(code, message, throwable, args);
        }
        if (ExceptionTypeEnums.BUSINESS_EXCEPTION.equals(exceptionTypeEnums)) {
            return new BusinessException(code, message, throwable, args);
        }
        return null;
    }
}
