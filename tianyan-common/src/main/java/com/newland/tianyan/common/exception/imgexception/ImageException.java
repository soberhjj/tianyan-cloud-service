package com.newland.tianyan.common.exception.imgexception;

/**
 * @Author: huangJunJie  2021-02-26 14:27
 */
public class ImageException extends RuntimeException {

    private static final long serialVersionUID = 1L;

    private int code;

    public ImageException(int code, String msg) {
        super(msg);
        this.code = code;
    }

    public ImageException(ImageExceptionReturn imageExceptionReturn) {
        super(imageExceptionReturn.getMsg());
        this.code = imageExceptionReturn.getCode();
    }

    public int getCode() {
        return code;
    }
}
