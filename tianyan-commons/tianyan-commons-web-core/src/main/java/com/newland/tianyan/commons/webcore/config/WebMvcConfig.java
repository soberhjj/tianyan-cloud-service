package com.newland.tianyan.commons.webcore.config;


import com.newland.tianya.commons.base.utils.ServerAddressUtils;
import com.newland.tianyan.commons.webcore.intercept.ApiMethodLogIntercept;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * @author: RojiaHuang
 * @description:
 * @date: 2021/2/25
 */
@Configuration
public class WebMvcConfig implements WebMvcConfigurer {

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(new ApiMethodLogIntercept(serverAddressUtils));
    }

    @Autowired
    private ServerAddressUtils serverAddressUtils;

}
