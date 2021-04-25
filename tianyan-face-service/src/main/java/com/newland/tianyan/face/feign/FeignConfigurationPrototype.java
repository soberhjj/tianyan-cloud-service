package com.newland.tianyan.face.feign;

import feign.Feign;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Scope;

/**
 * @author: RojiaHuang
 * @description:
 * @date: 2021/4/20
 */
public class FeignConfigurationPrototype {
    @Bean
    @Scope("prototype")
    public Feign.Builder feignBuilder(){
        return new Feign.Builder();
    }
}
