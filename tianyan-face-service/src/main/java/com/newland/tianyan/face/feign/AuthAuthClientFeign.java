package com.newland.tianyan.face.feign;


import com.newland.tianyan.common.api.IAuthClientApi;
import com.newland.tianyan.face.feign.fallback.AuthServiceFeignClientFallbackImpl;
import org.springframework.cloud.openfeign.FeignClient;

@FeignClient(name = "auth-service", fallbackFactory = AuthServiceFeignClientFallbackImpl.class)
public interface AuthAuthClientFeign extends IAuthClientApi {

}
