package com.newland.tianyan.image.handler;

import com.newland.tianyan.common.exception.imgexception.ImageException;
import com.newland.tianyan.common.utils.JsonErrorObject;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.io.IOException;

/**
 * @Author: huangJunJie  2021-02-27 10:26
 */
@Slf4j
@RestControllerAdvice
public class GloabalExceptionHandler {

    @ExceptionHandler(ImageException.class)
    public JsonErrorObject handleImageException(ImageException e) {
        log.info("code:{},msg:{}", e.getCode(), e.getMessage());
        return new JsonErrorObject("123456", e.getCode(), e.getMessage());
    }

    @ExceptionHandler(IOException.class)
    public JsonErrorObject handleIOException(IOException e) {
        log.info("code:{},msg:{}", 202110, "图片上传失败");
        return new JsonErrorObject("123456", 202110, "图片上传失败");
    }
}
