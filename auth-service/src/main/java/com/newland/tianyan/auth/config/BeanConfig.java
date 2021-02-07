package com.newland.tianyan.auth.config;

import org.springframework.cloud.alibaba.nacos.NacosDiscoveryProperties;
import org.springframework.cloud.client.loadbalancer.LoadBalanced;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @Author: huangJunJie  2021-02-07 15:13
 */
@Configuration
public class BeanConfig {

    @Bean
    @LoadBalanced
    public NacosDiscoveryProperties registerBean(){
        return new NacosDiscoveryProperties();
    }
}
