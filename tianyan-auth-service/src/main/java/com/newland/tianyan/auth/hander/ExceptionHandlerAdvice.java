//package com.newland.tianyan.auth.hander;
//
//import com.newland.tianya.commons.base.exception.ArgumentException;
//import com.newland.tianya.commons.base.model.JsonErrorObject;
//import com.newland.tianyan.commons.webcore.hander.BaseExceptionConvert;
//import lombok.extern.slf4j.Slf4j;
//import org.springframework.http.HttpStatus;
//import org.springframework.web.bind.MethodArgumentNotValidException;
//import org.springframework.web.bind.annotation.ExceptionHandler;
//import org.springframework.web.bind.annotation.ResponseBody;
//import org.springframework.web.bind.annotation.ResponseStatus;
//import org.springframework.web.bind.annotation.RestControllerAdvice;
//
///**
// * @author: RojiaHuang
// * @description:
// * @date: 2021/3/29
// */
//@RestControllerAdvice
//@Slf4j
//public class ExceptionHandlerAdvice {
//
//    @ExceptionHandler(Exception.class)
//    @ResponseStatus(HttpStatus.BAD_REQUEST)
//    @ResponseBody
//    public JsonErrorObject handleArgumentException2(Exception e) {
//        log.warn("抛出参数异常", e);
//        e.printStackTrace();
//        return BaseExceptionConvert.toJsonObject(e);
//    }
//}
