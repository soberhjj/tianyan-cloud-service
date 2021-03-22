package com.newland.tianyan.gateway.service;


import org.springframework.stereotype.Service;

import com.newland.tianyan.gateway.filter.ratelimit.DynamicRedisRateLimiter;
import com.newland.tianyan.gateway.model.RateLimitKey;

/**
 * @program: gateway-service
 * @description:
 * @author: nxw
 * @create: 2020-02-29 14:27
 **/
@Service
public interface IRedisService {

	/**
	 *       获取用户对应的限流配置
	 * @param rateLimitKey
	 * @param routeId
	 * @return
	 */
    DynamicRedisRateLimiter.Config getAccountLimit(RateLimitKey rateLimitKey, String routeId);
}
