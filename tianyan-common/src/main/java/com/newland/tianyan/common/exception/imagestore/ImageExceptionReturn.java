package com.newland.tianyan.common.exception.imagestore;

/**
 * @Author: huangJunJie  2021-02-26 14:11
 */
public enum ImageExceptionReturn {

    BASE64_FORMAT_ILLEGAL(202100,"非BASE64编码串"),
    IMAGE_SIZE_OVER_2MB(202101,"图片大小超过2MB"),
    IMAGE_FORMAT_ILLEGAL(202102,"暂不支持JPG、PNG、BMP以外的图片格式");


    private int code;
    private String msg;

    ImageExceptionReturn(int code, String msg) {
        this.code = code;
        this.msg = msg;
    }

    public int getCode() {
        return code;
    }

    public String getMsg() {
        return msg;
    }
}
