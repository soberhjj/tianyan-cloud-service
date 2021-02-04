package com.newland.tianyan.common.feign.falback;

import org.springframework.context.annotation.Bean;

/**
 * @author: RojiaHuang
 * @description:
 * @date: 2021/2/2
 */
public class FeignConfiguration {
    @Bean
    public ImageServiceFeignClientFallbackImpl echoServiceFallback() {
        return new ImageServiceFeignClientFallbackImpl();
    }
}
