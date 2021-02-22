package com.newland.tianyan.face.remote;


import com.newland.tianyan.common.model.authservice.IClientApi;
import com.newland.tianyan.face.remote.fallback.AuthServiceFeignClientFallbackImpl;
import org.springframework.cloud.openfeign.FeignClient;

@FeignClient(name = "auth-service", fallbackFactory = AuthServiceFeignClientFallbackImpl.class)
public interface AuthClientFeign extends IClientApi {

}
