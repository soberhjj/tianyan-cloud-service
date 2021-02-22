package com.newland.tianyan.face.remote;


import com.newland.tianyan.common.api.IAuthClientApi;
import com.newland.tianyan.face.remote.fallback.AuthServiceFeignClientFallbackImpl;
import org.springframework.cloud.openfeign.FeignClient;

@FeignClient(name = "auth-service", fallbackFactory = AuthServiceFeignClientFallbackImpl.class)
public interface AuthAuthClientFeign extends IAuthClientApi {

}
