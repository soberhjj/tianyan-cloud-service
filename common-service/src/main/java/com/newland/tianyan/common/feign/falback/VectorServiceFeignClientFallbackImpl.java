package com.newland.tianyan.common.feign.falback;

import com.newland.tianyan.common.feign.VectorSearchFeignService;
import feign.hystrix.FallbackFactory;

/**
 * @author: RojiaHuang
 * @description:
 * @date: 2021/2/8
 */
public class VectorServiceFeignClientFallbackImpl implements FallbackFactory<VectorSearchFeignService> {

    @Override
    public VectorSearchFeignService create(Throwable throwable) {
        throw new RuntimeException("远程服务器发生错误,请稍后再次尝试");
    }
}
