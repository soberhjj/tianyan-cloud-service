//package com.newland.tianyan.face.aop;
//
//
//import com.alibaba.fastjson.JSON;
//import com.newland.tianyan.common.exception.CommonException;
//import com.newland.tianyan.common.utils.JsonErrorObject;
//import com.newland.tianyan.common.utils.LogUtils;
//import feign.FeignException;
//import org.springframework.http.HttpStatus;
//import org.springframework.validation.FieldError;
//import org.springframework.web.bind.MethodArgumentNotValidException;
//import org.springframework.web.bind.annotation.ExceptionHandler;
//import org.springframework.web.bind.annotation.ResponseBody;
//import org.springframework.web.bind.annotation.ResponseStatus;
//import org.springframework.web.bind.annotation.RestControllerAdvice;
//
//import javax.persistence.EntityExistsException;
//import javax.persistence.EntityNotFoundException;
//import java.nio.ByteBuffer;
//import java.util.List;
//import java.util.Optional;
//
///**
// * @Author: huangJunJie  2020-12-01 11:33
// */
//@RestControllerAdvice
//public class GlobalExceptionHandler {
//
//    private static final String NULL = "param[%s] is null";
//    private static final String MIN = "param[%s] value is too small";
//    private static final String EMPTY = "param[%s] is empty";
//    private static final String PATTERN = "param[%s] format error";
//    private static final String RANGE = "param[%s] out of range";
//    private static final String BLANK = "param[%s] is blank";
//    private static final String NOT_EMPTY_TEMPLATE = "not enough param, #%s# is mandatory";
//    private static final String POSITIVE_TEMPLATE = "not enough param, #%s# should be positive";
//    private static final String OUT_OF_RANG_TEMPLATE = "not enough param, #%s# out of range";
//
//    @ExceptionHandler(MethodArgumentNotValidException.class)
//    public JsonErrorObject handleBindException(MethodArgumentNotValidException exception) {
//        List<FieldError> fieldErrors = exception.getBindingResult().getFieldErrors();
//        for (FieldError fieldError : fieldErrors) {
//            System.out.println(fieldError.getField() + " : " + fieldError.getCode() + " : " + fieldError.getDefaultMessage());
//        }
//
//        FieldError fieldError = exception.getBindingResult().getFieldError();
//        return getErrorObject(fieldError);
//    }
//
//    private JsonErrorObject getErrorObject(FieldError fieldError) {
//        String code = fieldError.getCode();
//        String field = fieldError.getField();
//        switch (code) {
//            case "NotNull":
//                return new JsonErrorObject(LogUtils.traceId(), 363001, String.format(NULL, field));
//            case "Min":
//                return new JsonErrorObject(LogUtils.traceId(), 363002, String.format(MIN, field));
//            case "NotEmpty":
//                return new JsonErrorObject(LogUtils.traceId(), 363003, String.format(EMPTY, field));
//            case "Pattern":
//                return new JsonErrorObject(LogUtils.traceId(), 363004, String.format(PATTERN, field));
//            case "Range":
//                return new JsonErrorObject(LogUtils.traceId(), 363005, String.format(RANGE, field));
//            default:
//                return new JsonErrorObject(LogUtils.traceId(), 363006, "parameters error");
//        }
//    }
//
//
//    @ExceptionHandler({EntityNotFoundException.class, EntityExistsException.class})
//    public JsonErrorObject entityException(Exception e) {
//        return new JsonErrorObject(LogUtils.traceId(), 363010, e.getMessage());
//    }
//
//    @ExceptionHandler(CommonException.class)
//    public JsonErrorObject commonException(CommonException exception) {
//        return new JsonErrorObject(LogUtils.traceId(), 363020, exception.getErrorMsg());
//    }
//
//    @ExceptionHandler(FaceApiException.class)
//    public JsonErrorObject commonException(FaceApiException exception) {
//        return new JsonErrorObject(LogUtils.traceId(), exception.getErrorCode(), exception.getErrorMsg());
//    }
//
//    @ExceptionHandler(Exception.class)
//    public JsonErrorObject handleBindException(Exception exception) {
//        exception.printStackTrace();
//        return new JsonErrorObject(LogUtils.traceId(), 365000, "system error,cause: " + exception.getMessage());
//    }
//
//    @ExceptionHandler(FeignException.class)
//    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
//    @ResponseBody
//    public JsonErrorObject handleFeignException(FeignException exception) {
//        exception.printStackTrace();
//        Optional<ByteBuffer> byteBuffer = exception.responseBody();
//        if (byteBuffer.isPresent()) {
//            String responseJson = byteBuffer.get().toString();
//            JsonErrorObject responseBaseDTO = JSON.parseObject(responseJson, JsonErrorObject.class);
//            return new JsonErrorObject(LogUtils.traceId(), responseBaseDTO.getErrorCode(), responseBaseDTO.getErrorMsg());
//        }
//        return new JsonErrorObject(LogUtils.traceId(), 365000, "system error,cause: " + exception.getMessage());
//    }
//}
