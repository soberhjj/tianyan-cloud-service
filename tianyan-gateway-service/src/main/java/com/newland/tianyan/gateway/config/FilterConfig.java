package com.newland.tianyan.gateway.config;

import com.newland.tianyan.gateway.filter.AuthGlobalFilter;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @Author: huangJunJie  2021-03-04 16:33
 */
@Configuration
public class FilterConfig {
    @Bean
    public GlobalFilter globalFilter() {
        return new AuthGlobalFilter();
    }
}
