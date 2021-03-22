package com.newland.tianya.commons.base.support;


import com.newland.tianya.commons.base.exception.ArgumentException;
import com.newland.tianya.commons.base.exception.BaseException;
import com.newland.tianya.commons.base.exception.BusinessException;
import com.newland.tianya.commons.base.exception.SysException;


/**
 * @author: RojiaHuang
 * @description:
 * @date: 2021/3/20
 */
public class ExceptionSupport {

    public static BaseException toException(IExceptionEnums iExceptionSupport) {
        return toException(iExceptionSupport, null, (Object) null);
    }

    public static BaseException toException(IExceptionEnums iExceptionSupport, Object... args) {
        return toException(iExceptionSupport, null, args);
    }

    public static BaseException toException(IExceptionEnums iExceptionSupport, Throwable throwable) {
        return toException(iExceptionSupport, null, throwable);
    }

    public static BaseException toException(IExceptionEnums iExceptionSupport, Throwable throwable, Object... args) {
        switch (iExceptionSupport.getTypeEnums()) {
            case ARGUMENT_EXCEPTION:
                return new ArgumentException(iExceptionSupport.getErrorCode(), iExceptionSupport.getErrorMsg(), throwable, args;
            case BUSINESS_EXCEPTION:
                return new BusinessException(iExceptionSupport.getErrorCode(), iExceptionSupport.getErrorMsg(), throwable, args);
            case SYSTEM_EXCEPTION:
                return new SysException(iExceptionSupport.getErrorCode(), iExceptionSupport.getErrorMsg(), throwable, args);
            default:
                return new SysException(iExceptionSupport.getErrorCode(), iExceptionSupport.getErrorMsg(), iExceptionSupport.getErrorMsg(), throwable, args);
        }
    }
}
