package com.newland.tianyan.gateway.filter.ratelimit;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.gateway.filter.ratelimit.KeyResolver;
import org.springframework.web.server.ServerWebExchange;

import com.alibaba.fastjson.JSON;
import com.newland.tianya.commons.base.constants.TokenConstants;
import com.newland.tianyan.gateway.model.RateLimitKey;

import reactor.core.publisher.Mono;

/**
 *   根据用户限流
 * @author sj
 *
 */
public class UserKeyResolver implements KeyResolver {

    public static final String BEAN_NAME = "userKeyResolver";
    
    @Value("${spring.profiles.active}")
    private String environment;

    @Override
    public Mono<String> resolve(ServerWebExchange exchange) {
    	RateLimitKey rateLimitKey = new RateLimitKey();
    	rateLimitKey.setEnvironment(environment);
    	rateLimitKey.setAccount(exchange.getRequest().getHeaders().getFirst(TokenConstants.HEAD_ACCOUNT));
        return Mono.just(JSON.toJSONString(rateLimitKey));
    }

}