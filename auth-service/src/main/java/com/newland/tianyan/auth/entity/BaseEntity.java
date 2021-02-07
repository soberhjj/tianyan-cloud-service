package com.newland.tianyan.auth.entity;

import javax.persistence.Transient;

/**
 * 用于获取分页参数
 */
public class BaseEntity {

    @Transient
    private Integer startIndex = 0;

    @Transient
    private Integer length = 100;

    public Integer getStartIndex() {
        return startIndex;
    }

    public void setStartIndex(Integer startIndex) {
        this.startIndex = startIndex;
    }

    public Integer getLength() {
        return length;
    }

    public void setLength(Integer length) {
        this.length = length;
    }
}
