package com.newland.tianyan.common.constans;

/**
 * @Author: huangJunJie  2021-02-24 08:50
 */
public enum ImageType {
    /**
     * jpg
     */
    JPG(0xFFD8),
    /**
     * png
     */
    PNG(0x8950),
    /**
     * bmp
     */
    BMP(0x424D);

    private final int rule;

    private ImageType(int rule) {
        this.rule = rule;
    }

    public int getRule() {
        return rule;
    }

}
