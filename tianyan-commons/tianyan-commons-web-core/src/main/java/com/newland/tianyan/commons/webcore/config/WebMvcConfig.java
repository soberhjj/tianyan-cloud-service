package com.newland.tianyan.commons.webcore.config;


import com.newland.tianya.commons.base.utils.ServerAddressUtils;
import com.newland.tianyan.commons.webcore.intercept.ApiMethodLogIntercept;
import com.newland.tianyan.commons.webcore.resolver.ApiArgumentResolver;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;

import java.util.List;

/**
 * @author: RojiaHuang
 * @description:
 * @date: 2021/2/25
 */
@Configuration
public class WebMvcConfig implements WebMvcConfigurer {

    @Autowired
    private ServerAddressUtils serverAddressUtils;

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(new ApiMethodLogIntercept(serverAddressUtils))
                .excludePathPatterns("/swagger-ui.html")
                .excludePathPatterns("/swagger-resources");
    }
}
