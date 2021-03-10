package com.newland.tianyan.face.feign;

import com.alibaba.fastjson.JSON;
import com.newland.tianyan.common.utils.JsonErrorObject;
import feign.FeignException;
import feign.Response;
import feign.Util;
import feign.codec.ErrorDecoder;

import java.nio.charset.StandardCharsets;

/**
 * @author: RojiaHuang
 * @description:
 * @date: 2021/3/3
 */
public class FeignErrorDecoder implements ErrorDecoder {
    @Override
    public Exception decode(String methodKey, Response response) {
        try {
            if (response.body() != null) {
                String content = Util.toString(response.body().asReader(StandardCharsets.UTF_8));
                JsonErrorObject exceptionInfo = JSON.parseObject(content, JsonErrorObject.class);
                return new ReportException(exceptionInfo.getErrorCode(), exceptionInfo.getErrorMsg());
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        return FeignException.errorStatus(methodKey, response);
    }
}
