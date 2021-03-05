package com.newland.tianyan.image.exception;

import com.newland.tianyan.common.exception.global.business.BusinessException;

/**
 * @author: RojiaHuang
 * @description: 业务异常
 * @date: 2021/3/3
 */
public enum BusinessErrorEnums {
    /**
     * 测试微服务异常
     */
    DEMO(1, "demo exception"),
    BASE64_FORMAT_ILLEGAL(202100,"非BASE64编码串"),
    IMAGE_SIZE_OVER_2MB(202101,"图片大小超过2MB"),
    IMAGE_FORMAT_ILLEGAL(202102,"暂不支持JPG、PNG、BMP以外的图片格式"),
    ;
    private int errorCode;

    private String errorMsg;

    public int getErrorCode() {
        return errorCode;
    }

    public String getErrorMsg() {
        return errorMsg;
    }

    BusinessErrorEnums(int errorCode, String errorMsg) {
        this.errorCode = errorCode;
        this.errorMsg = errorMsg;
    }

    public BusinessException toException() {
        return new BusinessException(this.getErrorCode(), this.getErrorMsg());
    }


    public BusinessException toException(Object... args) {
        return new BusinessException(this.getErrorCode(), this.getErrorMsg(), args);
    }

}
