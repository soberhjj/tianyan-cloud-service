package com.newland.tianyan.gateway.config;

import com.newland.tianyan.gateway.filter.AuthGlobalFilter;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * 网关配置类
 *
 * @Author: huangJunJie  2021-03-04 16:33
 */
@Configuration
public class GlobalGatewayConfig {
    @Bean
    public GlobalFilter globalFilter() {
        return new AuthGlobalFilter();
    }
}
