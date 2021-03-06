package com.newland.tianyan.common.aop;


import com.newland.tianyan.common.constans.ArgumentErrorEnums;
import com.newland.tianyan.common.exception.ArgumentException;
import com.newland.tianyan.common.exception.BusinessException;
import com.newland.tianyan.common.exception.SysException;
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
     * 参数异常,hibernate验证参数
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    @ResponseBody
    public JsonErrorObject handleArgumentException(MethodArgumentNotValidException e) {
        log.warn("抛出参数异常", e);
        FieldError fieldError = e.getBindingResult().getFieldError();
        ArgumentException commonException = getError(fieldError.getCode(), fieldError.getField());
        return new JsonErrorObject(LogUtils.traceId(), commonException.getErrorCode(), commonException.getErrorMsg());
    }

    /**
     * 业务异常,非hibernate验证
     */
    @ExceptionHandler(ArgumentException.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    @ResponseBody
    public JsonErrorObject handleArgumentException2(ArgumentException e) {
        log.warn("抛出参数异常", e);
        e.printStackTrace();
        return new JsonErrorObject(LogUtils.traceId(), e.getErrorCode(), e.getErrorMsg());
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
     * 系统异常(未知)
     */
    @ExceptionHandler({Exception.class, SysException.class})
    @ResponseBody
    public JsonErrorObject handleOtherException(Exception e) {
        log.warn("抛出系统异常", e);
        e.printStackTrace();
        return new JsonErrorObject(LogUtils.traceId(), 6000, "system error");
    }

    private static ArgumentException getError(String code, String field) {
        switch (code) {
            case "NotBlank":
                return ArgumentErrorEnums.ARGUMENT_NOT_BLANK.toException(field);
            case "NotNull":
                return ArgumentErrorEnums.ARGUMENT_NOT_NULL.toException(field);
            case "NotEmpty":
                return ArgumentErrorEnums.ARGUMENT_NOT_EMPTY.toException(field);
            case "Max":
                return ArgumentErrorEnums.ARGUMENT_SIZE_MAX.toException(field);
            case "Min":
                return ArgumentErrorEnums.ARGUMENT_SIZE_MIN.toException(field);
            case "Pattern":
                return ArgumentErrorEnums.ARGUMENT_PATTERN.toException(field);
            default:
                return ArgumentErrorEnums.ARGUMENT_FORMAT_ERROR.toException(field);
        }
    }
}
