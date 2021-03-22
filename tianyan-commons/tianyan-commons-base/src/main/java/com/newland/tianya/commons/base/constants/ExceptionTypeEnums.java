package com.newland.tianya.commons.base.constants;


import lombok.Getter;

/**
 * @author: RojiaHuang
 * @description:
 * @date: 2021/3/22
 */
@Getter
public enum ExceptionTypeEnums {
    /**
     * 系统异常
     */
    SYSTEM_EXCEPTION("SysException.class"),
    /**
     * 业务异常
     */
    BUSINESS_EXCEPTION("BusinessException.class"),
    /**
     * 参数异常
     */
    ARGUMENT_EXCEPTION("ArgumentException.class"),
    ;
    private final String typeClass;

    ExceptionTypeEnums(String typeClass) {
        this.typeClass = typeClass;
    }

}
