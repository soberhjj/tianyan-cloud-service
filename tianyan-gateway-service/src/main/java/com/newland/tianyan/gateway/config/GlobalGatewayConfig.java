package com.newland.tianyan.gateway.config;

import java.util.List;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.cloud.gateway.filter.ratelimit.RedisRateLimiter;
import org.springframework.cloud.gateway.support.ConfigurationService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.data.redis.core.ReactiveStringRedisTemplate;
import org.springframework.data.redis.core.script.RedisScript;

import com.newland.tianyan.gateway.filter.AuthGlobalFilter;
import com.newland.tianyan.gateway.filter.ratelimit.DynamicRedisRateLimiter;
import com.newland.tianyan.gateway.filter.ratelimit.UserKeyResolver;

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
    @Bean
    @Primary
    public UserKeyResolver userKeyResolver() {
        return new UserKeyResolver();
    }
 
    @Bean
    @ConditionalOnMissingBean
    public RedisRateLimiter redisRateLimiter(ReactiveStringRedisTemplate redisTemplate, 
    										@Qualifier("redisRequestRateLimiterScript") RedisScript<List<Long>> redisScript, 
    										ConfigurationService configurationService) {
        return new RedisRateLimiter(redisTemplate, redisScript, configurationService);
        
    }
    
    @Bean(name = "dynamicRedisRateLimiter")
    @Primary
    public DynamicRedisRateLimiter dynamicRedisRateLimiter(ReactiveStringRedisTemplate redisTemplate,
                                                    @Qualifier("redisRequestRateLimiterScript") RedisScript<List<Long>> redisScript,
                                                    ConfigurationService configurationService) {
        return new DynamicRedisRateLimiter(redisTemplate, redisScript, configurationService);
    }
}
