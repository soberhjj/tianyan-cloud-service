package com.newland.tianyan.common.feign.falback;


import feign.hystrix.FallbackFactory;
import org.springframework.stereotype.Component;

/**
 * @author: RojiaHuang
 * @description: 降级处理
 * @date: 2021/2/2
 */
@Component
public class AuthServiceFeignClientFallbackImpl implements FallbackFactory<AuthServiceFeignClientFallbackImpl> {

    @Override
    public AuthServiceFeignClientFallbackImpl create(Throwable throwable) {
        return null;
    }
}
