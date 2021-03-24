package com.newland.tianya.commons.base.support;


import com.newland.tianya.commons.base.constants.GlobalExceptionEnum;
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
                return new ArgumentException(iExceptionSupport.getErrorCode(), iExceptionSupport.getErrorMsg(), throwable, args);
            case BUSINESS_EXCEPTION:
                return new BusinessException(iExceptionSupport.getErrorCode(), iExceptionSupport.getErrorMsg(), throwable, args);
            case SYSTEM_EXCEPTION:
                return new SysException(iExceptionSupport.getErrorCode(), iExceptionSupport.getErrorMsg(), throwable, args);
            default:
                return new SysException(iExceptionSupport.getErrorCode(), iExceptionSupport.getErrorMsg(), iExceptionSupport.getErrorMsg(), throwable, args);
        }
    }

    public static BaseException figureOutException(Exception exception) {
        String clientSecretErrorMsg = "用户名或密码错误";
        String grantTypeUnauthorizedErrorMsg = "Unauthorized grant type";
        String grantTypeUnsupportedErrorMsg = "Unsupported grant type";
        GlobalExceptionEnum errorEnums = null;
        if (clientSecretErrorMsg.equals((exception.getMessage()))) {
            errorEnums = GlobalExceptionEnum.CLIENT_SECRET_ERROR;
        }

        if (exception.getMessage().contains(grantTypeUnauthorizedErrorMsg)) {
            errorEnums = GlobalExceptionEnum.GRANT_TYPE_INVALID;
        }

        if (exception.getMessage().contains(grantTypeUnsupportedErrorMsg)) {
            errorEnums = GlobalExceptionEnum.GRANT_TYPE_INVALID;
        }
        //未知类型封装
        if (errorEnums == null) {
            errorEnums = GlobalExceptionEnum.SYSTEM_ERROR;
        }
        return toException(errorEnums);
    }
}
