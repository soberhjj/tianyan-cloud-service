package com.newland.tianyan.common.version;

import org.springframework.boot.autoconfigure.web.servlet.WebMvcRegistrations;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerMapping;

/**
 * @author: https://mp.weixin.qq.com/s/m2HnUBXagKaLQjzww1s77g
 * @description:
 * @date: 2021/2/22
 */
@Configuration
public class VersionConfiguration implements WebMvcRegistrations {
    @Bean
    protected RequestMappingHandlerMapping customRequestMappingHandlerMapping() {
        return new VersionRequestMappingHandlerMapping();
    }
    @Override
    public RequestMappingHandlerMapping getRequestMappingHandlerMapping() {
        RequestMappingHandlerMapping handlerMapping = new VersionRequestMappingHandlerMapping();
        handlerMapping.setOrder(0);
        return handlerMapping;
    }
}
