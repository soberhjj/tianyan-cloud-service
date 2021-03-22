package com.newland.tianyan.commons.webcore.hander;


import com.newland.tianya.commons.base.constants.GlobalExceptionEnum;
import com.newland.tianya.commons.base.exception.ArgumentException;
import com.newland.tianya.commons.base.exception.BaseException;
import com.newland.tianya.commons.base.exception.BusinessException;
import com.newland.tianya.commons.base.exception.SysException;
import com.newland.tianya.commons.base.support.ExceptionSupport;
import com.newland.tianya.commons.base.utils.JsonErrorObject;
import com.newland.tianya.commons.base.utils.LogUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.validation.FieldError;
import org.springframework.web.HttpMediaTypeNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.NoHandlerFoundException;

import javax.servlet.http.HttpServletRequest;
import java.sql.SQLException;
import java.sql.SQLSyntaxErrorException;
import java.util.Objects;

/**
 * @author Administrator
 * @description: 全局异常捕获处理方法
 */
@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    /**
     * 业务异常,非hibernate验证
     */
    @ExceptionHandler(ArgumentException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ResponseBody
    public JsonErrorObject handleArgumentException2(ArgumentException e) {
        log.warn("抛出参数异常", e);
        e.printStackTrace();
        return toJsonObject(e);
    }

    /**
     * 业务异常
     */
    @ExceptionHandler(BusinessException.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    @ResponseBody
    public JsonErrorObject handleBusinessException(BusinessException e) {
        log.warn("抛出业务异常:[{}:{}],{}", e.getErrorCode(), e.getErrorMsg(), e);
        return toJsonObject(e);
    }

    /**
     * 业务异常
     */
    @ExceptionHandler(SysException.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    @ResponseBody
    public JsonErrorObject handleSystemException(SysException e) {
        log.warn("抛出系统异常", e);
        e.printStackTrace();
        return toJsonObjectWithDefaultMsg(e, "system busy");
    }


    /**
     * 系统异常(未知)
     * 已知系统异常向外暴露错误码，但统一展示系统繁忙
     * 未知异常异常向外暴露6000错误码+system error
     */
    @ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public JsonErrorObject handleOtherException(Exception e) {
        log.warn("抛出系统异常", e);
        e.printStackTrace();
        return toJsonObject(ExceptionSupport.toException(GlobalExceptionEnum.SYSTEM_ERROR));
    }

    /**
     * 参数异常,hibernate验证参数
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public JsonErrorObject handleArgumentException(MethodArgumentNotValidException e) {
        log.warn("抛出参数异常", e);
        FieldError fieldError = e.getBindingResult().getFieldError();
        assert fieldError != null;
        ArgumentException argumentException = getError(Objects.requireNonNull(fieldError.getCode()), fieldError.getField());
        return toJsonObject(argumentException);
    }

    /**
     * httpContent非json格式
     */
    @ExceptionHandler(HttpMediaTypeNotSupportedException.class)
    @ResponseStatus(HttpStatus.UNSUPPORTED_MEDIA_TYPE)
    public JsonErrorObject handleMediaTypeException(HttpMediaTypeNotSupportedException e) {
        log.warn("抛出http异常", e);
        if (!MediaType.APPLICATION_JSON.getType().equals(Objects.requireNonNull(e.getContentType()).getType())) {
            return toJsonObject(ExceptionSupport.toException(GlobalExceptionEnum.JSON_CONTENT_FORMAT_ERROR));
        } else {
            return toJsonObject(ExceptionSupport.toException(GlobalExceptionEnum.SYSTEM_ERROR));
        }
    }

    /**
     * 非法匹配路径【404】
     */
    @ExceptionHandler(NoHandlerFoundException.class)
    @ResponseStatus(HttpStatus.METHOD_NOT_ALLOWED)
    public JsonErrorObject handleNoHandlerException(NoHandlerFoundException e, HttpServletRequest request) {
        log.warn("抛出参数[URI不支持]异常", e);
        return toJsonObject(ExceptionSupport.toException(GlobalExceptionEnum.INVALID_METHOD, request.getRequestURI()));
    }

    /**
     * SQL异常
     */
    @ExceptionHandler({SQLException.class, SQLSyntaxErrorException.class})
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public JsonErrorObject handleSqlException(Exception e) {
        log.warn("抛出SQL异常", e);
        return toJsonObject(ExceptionSupport.toException(GlobalExceptionEnum.SQL_NOT_VALID));
    }

    protected static JsonErrorObject toJsonObject(BaseException baseException) {
        return new JsonErrorObject(LogUtils.traceId(), baseException.getErrorCode(), baseException.getErrorMsg());
    }

    protected static JsonErrorObject toJsonObjectWithDefaultMsg(BaseException baseException, String defaultErrorMsg) {
        return new JsonErrorObject(LogUtils.traceId(), baseException.getErrorCode(), defaultErrorMsg);
    }

    protected static ArgumentException getError(String code, String field) {
        GlobalExceptionEnum errorEnums;
        switch (code) {
            case "NotBlank":
                errorEnums = GlobalExceptionEnum.ARGUMENT_NOT_BLANK;
                break;
            case "NotNull":
                errorEnums = GlobalExceptionEnum.ARGUMENT_NOT_NULL;
                break;
            case "NotEmpty":
                errorEnums = GlobalExceptionEnum.ARGUMENT_NOT_EMPTY;
                break;
            case "Max":
                errorEnums = GlobalExceptionEnum.ARGUMENT_SIZE_MAX;
                break;
            case "Min":
                errorEnums = GlobalExceptionEnum.ARGUMENT_SIZE_MIN;
                break;
            case "Pattern":
                errorEnums = GlobalExceptionEnum.ARGUMENT_PATTERN;
                break;
            default:
                errorEnums = GlobalExceptionEnum.ARGUMENT_FORMAT_ERROR;
        }
        return (ArgumentException) ExceptionSupport.toException(errorEnums);
    }
}