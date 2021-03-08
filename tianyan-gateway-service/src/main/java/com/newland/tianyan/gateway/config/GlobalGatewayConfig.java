package com.newland.tianyan.gateway.config;

import com.newland.tianyan.gateway.filter.AuthGlobalFilter;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @Author: huangJunJie  2021-03-04 16:33
 */
@Configuration
public class GlobalGatewayConfig {
    @Bean
    public GlobalFilter globalFilter() {
        return new AuthGlobalFilter();
    }

//    @Primary
//    @Bean
//    @Order(Ordered.HIGHEST_PRECEDENCE)
//    public ErrorWebExceptionHandler errorWebExceptionHandler(ObjectProvider<List<ViewResolver>> viewResolversProvider,
//                                                             ServerCodecConfigurer serverCodecConfigurer) {
//        JsonExceptionHandler globalGatewayExceptionHandler = new JsonExceptionHandler();
//        globalGatewayExceptionHandler.setViewResolvers(viewResolversProvider.getIfAvailable(Collections::emptyList));
//        globalGatewayExceptionHandler.setMessageWriters(serverCodecConfigurer.getWriters());
//        globalGatewayExceptionHandler.setMessageReaders(serverCodecConfigurer.getReaders());
//        return globalGatewayExceptionHandler;
//    }
}
