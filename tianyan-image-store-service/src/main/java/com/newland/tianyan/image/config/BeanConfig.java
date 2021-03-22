package com.newland.tianyan.image.config;

import com.newland.tianyan.common.utils.LogFixColumnUtils;
import com.newland.tianyan.common.utils.ServerAddressUtils;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @Author: huangJunJie  2021-03-09 10:31
 */
@Configuration
public class BeanConfig {

    @Bean
    LogFixColumnUtils logFixColumnUtils(){
        return new LogFixColumnUtils();
    }

    @Bean
    ServerAddressUtils serverAddressUtils(){
        return new ServerAddressUtils();
    }
}
