package com.newland.tianyan.face.feign;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author: RojiaHuang
 * @description:
 * @date: 2021/2/26
 */
@Configuration
public class FeignConfiguration {
    @Bean
    public FeignTraceInterceptor feignTraceInterceptor(){
        return new FeignTraceInterceptor();
    }
}
