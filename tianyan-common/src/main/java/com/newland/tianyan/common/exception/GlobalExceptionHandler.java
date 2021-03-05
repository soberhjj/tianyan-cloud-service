package com.newland.tianyan.common.exception;


import com.newland.tianyan.common.exception.global.argument.ArgumentErrorCategory;
import com.newland.tianyan.common.exception.global.argument.ArgumentException;
import com.newland.tianyan.common.exception.global.business.BusinessException;
import com.newland.tianyan.common.exception.global.system.SystemErrorEnums;
import com.newland.tianyan.common.exception.global.system.SysException;
import com.newland.tianyan.common.utils.JsonErrorObject;
import com.newland.tianyan.common.utils.LogUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

/**
 * @author Administrator
 * @description: 全局异常捕获处理方法
 */
@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {
    /**
     * 参数异常
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    @ResponseBody
    public JsonErrorObject handleArgumentException(MethodArgumentNotValidException e) {
        log.warn("抛出参数异常", e);
        FieldError fieldError = e.getBindingResult().getFieldError();
        ArgumentException commonException = ArgumentErrorCategory.getError(fieldError.getCode(), fieldError.getField());
        return new JsonErrorObject(LogUtils.traceId(), commonException.getErrorCode(), commonException.getErrorMsg());
    }

    /**
     * 业务异常
     */
    @ExceptionHandler(BusinessException.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    @ResponseBody
    public JsonErrorObject handleBusinessException(BusinessException e) {
        log.warn("抛出业务异常", e);
        e.printStackTrace();
        return new JsonErrorObject(LogUtils.traceId(), e.getErrorCode(), e.getErrorMsg());
    }

    /**
     * 系统异常
     */
    @ExceptionHandler(SysException.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    @ResponseBody
    public JsonErrorObject handleSystemException(SysException e) {
        log.warn("抛出系统异常", e);
        e.printStackTrace();
        return new JsonErrorObject(LogUtils.traceId(), e.getErrorCode(), e.getErrorMsg());
    }

    /**
     * 系统异常(未知)
     */
    @ExceptionHandler(Exception.class)
    @ResponseBody
    public JsonErrorObject handleOtherException(Exception e) {
        log.warn("抛出系统异常", e);
        e.printStackTrace();
        SysException sysException = SystemErrorEnums.SYSTEM_ERROR.toException();
        return new JsonErrorObject(LogUtils.traceId(), sysException.getErrorCode(), sysException.getErrorMsg());
    }
}
