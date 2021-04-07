package com.newland.tianyan.face.service;

import com.newland.tianya.commons.base.exception.BaseException;

/**
 * @author: RojiaHuang
 * @description:
 * @date: 2021/4/2
 */
public interface IQualityCheckService {

    void checkQuality(int qualityControl, String image) throws BaseException;
}
