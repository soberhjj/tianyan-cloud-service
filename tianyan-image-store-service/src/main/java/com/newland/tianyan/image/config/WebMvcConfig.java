package com.newland.tianyan.image.config;

import com.newland.tianyan.common.log.ApiMethodLogIntercept;
import com.newland.tianyan.common.log.LogFixColumnsHelper;
import com.newland.tianyan.common.log.ServerAddressHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.util.List;

/**
 * @author: RojiaHuang
 * @description:
 * @date: 2021/2/25
 */
@Configuration
public class WebMvcConfig implements WebMvcConfigurer {

    @Override
    public void addArgumentResolvers(List<HandlerMethodArgumentResolver> argumentResolvers) {
        //argumentResolvers.add(new ApiReqArgumentResolver());
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(new ApiMethodLogIntercept(logFixColumnsHelper, serverAddressHelper));
    }

    @Autowired
    private LogFixColumnsHelper logFixColumnsHelper;
    @Autowired
    private ServerAddressHelper serverAddressHelper;
}
