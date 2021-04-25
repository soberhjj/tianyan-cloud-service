package com.newland.tianyan.face.feign.fallback;



import com.newland.tianyan.face.feign.client.AuthClientFeignService;
import feign.hystrix.FallbackFactory;
import org.springframework.stereotype.Component;

/**
 * @author: RojiaHuang
 * @description: 降级处理
 * @date: 2021/2/2
 */
@Component
public class AuthServiceFeignClientFallbackImpl implements FallbackFactory<AuthClientFeignService> {

    @Override
    public AuthClientFeignService create(Throwable cause) {
        throw new RuntimeException("远程auth-service服务器发生错误,请稍后再次尝试");
    }
}
