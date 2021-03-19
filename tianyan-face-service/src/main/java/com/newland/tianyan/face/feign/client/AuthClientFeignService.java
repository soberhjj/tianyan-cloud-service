package com.newland.tianyan.face.feign.client;


import com.newland.tianyan.core.api.IAuthClientApi;
import com.newland.tianyan.common.model.auth.AuthClientReqDTO;
import com.newland.tianyan.face.feign.fallback.AuthServiceFeignClientFallbackImpl;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import javax.validation.Valid;

@FeignClient(name = "auth-service", fallbackFactory = AuthServiceFeignClientFallbackImpl.class)
public interface AuthClientFeignService extends IAuthClientApi {

    @PostMapping("/test")
    String test(@RequestBody @Valid AuthClientReqDTO request);
}
