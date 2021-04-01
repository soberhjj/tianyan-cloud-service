package com.newland.tianyan.commons.webcore.hander;

import com.newland.tianya.commons.base.exception.BaseException;
import com.newland.tianya.commons.base.model.JsonErrorObject;

/**
 * @author: RojiaHuang
 * @description:
 * @date: 2021/3/29
 */
public class BaseExceptionConvert {
    public static JsonErrorObject toJsonObject(BaseException baseException) {
        return new JsonErrorObject(baseException.getErrorCode(), baseException.getErrorMsg());
    }

    public static JsonErrorObject toJsonObjectWithDefaultMsg(BaseException baseException, String defaultErrorMsg) {
        return new JsonErrorObject(baseException.getErrorCode(), defaultErrorMsg);
    }
}
