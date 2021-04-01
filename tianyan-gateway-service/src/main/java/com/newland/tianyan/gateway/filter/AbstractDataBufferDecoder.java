package com.newland.tianyan.gateway.filter;

import org.springframework.core.codec.AbstractDecoder;

/**
 * @author: RojiaHuang
 * @description:
 * @date: 2021/4/1
 */
public abstract class AbstractDataBufferDecoder<T> extends AbstractDecoder<T> {
    private int maxInMemorySize = 5 * 1024 * 1024;;

    public void setMaxInMemorySize(int byteCount) {
        this.maxInMemorySize = byteCount;
    }

    public int getMaxInMemorySize() {
        return this.maxInMemorySize;
    }
}
