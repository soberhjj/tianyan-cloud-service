package com.newland.tianyan.face.aop;


import com.newland.tianyan.common.utils.exception.CommonException;
import com.newland.tianyan.common.utils.utils.JsonErrorObject;
import com.newland.tianyan.common.utils.utils.LogUtils;
import com.newland.tianyan.face.exception.ApiException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import javax.persistence.EntityExistsException;
import javax.persistence.EntityNotFoundException;
import java.util.List;

/**
 * @Author: huangJunJie  2020-12-01 11:33
 */
@RestControllerAdvice
public class GlobalExceptionHandler {

    private static final String NULL="param[%s] is null";
    private static final String Min="param[%s] value is too small";
    private static final String Empty="param[%s] is empty";
    private static final String Pattern="param[%s] format error";
    private static final String Range="param[%s] out of range";
    private static final String Blank="param[%s] is blank";
    private static final String NOT_EMPTY_TEMPLATE = "not enough param, #%s# is mandatory";
    private static final String POSITIVE_TEMPLATE = "not enough param, #%s# should be positive";
    private static final String OUT_OF_RANG_TEMPLATE = "not enough param, #%s# out of range";

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public JsonErrorObject handleBindException(MethodArgumentNotValidException exception) {
        List<FieldError> fieldErrors = exception.getBindingResult().getFieldErrors();
        for (FieldError fieldError:fieldErrors){
            System.out.println(fieldError.getField()+" : "+fieldError.getCode()+" : "+fieldError.getDefaultMessage());
        }

        FieldError fieldError = exception.getBindingResult().getFieldError();
        return getErrorObject(fieldError);
    }

    private JsonErrorObject getErrorObject(FieldError fieldError) {
        String code = fieldError.getCode();
        String field = fieldError.getField();
        switch (code) {
            case "NotNull":
                return new JsonErrorObject(LogUtils.getLogId(), 363001, String.format(NULL, field));
            case "Min":
                return new JsonErrorObject(LogUtils.getLogId(), 363002, String.format(Min, field));
            case "NotEmpty":
                return new JsonErrorObject(LogUtils.getLogId(), 363003, String.format(Empty, field));
            case "Pattern":
                return new JsonErrorObject(LogUtils.getLogId(), 363004, String.format(Pattern, field));
            case "Range":
                return new JsonErrorObject(LogUtils.getLogId(), 363005, String.format(Range, field));
            default:
                return new JsonErrorObject(LogUtils.getLogId(), 363006, "parameters error");
        }
    }


    @ExceptionHandler({EntityNotFoundException.class, EntityExistsException.class})
    public JsonErrorObject entityException(Exception e) {
        return new JsonErrorObject(LogUtils.getLogId(), 363010, e.getMessage());
    }

    @ExceptionHandler(CommonException.class)
    public JsonErrorObject commonException(CommonException exception) {
        return new JsonErrorObject(LogUtils.getLogId(), 363020, exception.getErrorMsg());
    }

    @ExceptionHandler(ApiException.class)
    public JsonErrorObject commonException(ApiException exception) {
        return new JsonErrorObject(LogUtils.getLogId(), exception.getErrorCode(), exception.getErrorMsg());
    }

    @ExceptionHandler(Exception.class)
    public JsonErrorObject handleBindException(Exception exception) {
        exception.printStackTrace();
        return new JsonErrorObject(LogUtils.getLogId(), 365000, "system error,cause: " + exception.getMessage());
    }
}
