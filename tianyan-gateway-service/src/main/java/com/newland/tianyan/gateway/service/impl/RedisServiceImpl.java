package com.newland.tianyan.gateway.service.impl;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import com.alibaba.fastjson.JSONObject;
import com.newland.tianyan.gateway.filter.ratelimit.DynamicRedisRateLimiter;
import com.newland.tianyan.gateway.model.RateLimitKey;
import com.newland.tianyan.gateway.service.IRedisService;

/**
 * redis查询服务
 * @author sj
 *
 */
@Service
public class RedisServiceImpl implements IRedisService{

	private static final String SPLIT_KEY = "###";
    @Autowired
    private RedisTemplate<String,String> redisTemplate;

    @Override
    public DynamicRedisRateLimiter.Config getAccountLimit(RateLimitKey rateLimitKey, String routeId) {
    	String redisKey = rateLimitKey.getEnvironment() + SPLIT_KEY + rateLimitKey.getAccount();
    	String limitConfig = (String)redisTemplate.opsForHash().get(redisKey,routeId);
        return limitConfig == null ? null : JSONObject.parseObject(limitConfig, DynamicRedisRateLimiter.Config.class);
    }
    
}
