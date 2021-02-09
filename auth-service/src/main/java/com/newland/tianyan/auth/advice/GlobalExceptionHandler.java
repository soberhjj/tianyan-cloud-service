package com.newland.tianyan.auth.advice;

import com.newland.tianyan.auth.exception.CommonException;
import com.newland.tianyan.common.utils.utils.JsonErrorObject;
import com.newland.tianyan.common.utils.utils.LogUtils;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import javax.persistence.EntityExistsException;
import javax.persistence.EntityNotFoundException;

/**
 * @author newland
 */
@RestControllerAdvice
public class GlobalExceptionHandler {

    private static final String NOT_EMPTY_TEMPLATE = "not enough param, #%s# is mandatory";
    private static final String POSITIVE_TEMPLATE = "not enough param, #%s# should be positive";
    private static final String OUT_OF_RANG_TEMPLATE = "not enough param, #%s# out of range";
    private static final String DEFAULT_MESSAGE = "invalid param #%s# ";

    @ExceptionHandler({IllegalArgumentException.class, EntityExistsException.class, EntityNotFoundException.class})
    public JsonErrorObject illegalArgumentException(Exception exception) {
        return new JsonErrorObject(LogUtils.getLogId(), 6001, exception.getMessage());
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public JsonErrorObject handleBindException(MethodArgumentNotValidException exception) {
        FieldError fieldError = exception.getBindingResult().getFieldError();
        return getErrorObject(fieldError);
    }

    @ExceptionHandler(CommonException.class)
    public JsonErrorObject commonException(CommonException exception) {
        int code = exception.getErrorCode();
        String msg = exception.getMsg();
        return new JsonErrorObject(LogUtils.getLogId(), code, msg);
    }

    private JsonErrorObject getErrorObject(FieldError fieldError) {
        String code = fieldError.getCode();
        String field = fieldError.getField();
        switch (code) {
            case "NotNull":
                return new JsonErrorObject(LogUtils.getLogId(), 6101, String.format(NOT_EMPTY_TEMPLATE, field));
            case "Min":
                return new JsonErrorObject(LogUtils.getLogId(), 6100, String.format(POSITIVE_TEMPLATE, field));
            case "Range":
                return new JsonErrorObject(LogUtils.getLogId(), 6100, String.format(OUT_OF_RANG_TEMPLATE, field));
            default:
                return new JsonErrorObject(LogUtils.getLogId(), 6100, String.format(DEFAULT_MESSAGE, field));
        }
    }
}
