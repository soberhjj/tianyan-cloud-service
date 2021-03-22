package com.newland.tianya.commons.base.support;

import com.newland.tianya.commons.base.constants.ExceptionTypeEnums;

/**
 * @author: RojiaHuang
 * @description:
 * @date: 2021/3/22
 */
public interface IExceptionEnums {

    int getErrorCode();

    String getErrorMsg();

    ExceptionTypeEnums getTypeEnums();
}
