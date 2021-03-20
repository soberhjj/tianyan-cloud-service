package com.newland.tianyan.image.constant;


import com.newland.tianya.commons.base.exception.BusinessException;

/**
 * @author: RojiaHuang
 * @description: 业务异常
 * @date: 2021/3/3
 */
public enum BusinessErrorEnums {
    /**
     * 测试微服务异常
     */
    BASE64_FORMAT_ILLEGAL(5350,"非BASE64编码串"),
    IMAGE_SIZE_OVER_2MB(5351,"图片大小超过2MB"),
    IMAGE_FORMAT_ILLEGAL(5352,"暂不支持JPG、PNG、BMP以外的图片格式"),
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
