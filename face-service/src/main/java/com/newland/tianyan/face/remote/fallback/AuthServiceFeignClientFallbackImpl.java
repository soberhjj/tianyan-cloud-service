package com.newland.tianyan.face.remote.fallback;



import com.newland.tianyan.face.remote.ClientFeign;
import feign.hystrix.FallbackFactory;
import org.springframework.stereotype.Component;

/**
 * @author: RojiaHuang
 * @description: 降级处理
 * @date: 2021/2/2
 */
@Component
public class AuthServiceFeignClientFallbackImpl implements FallbackFactory<ClientFeign> {

    @Override
    public ClientFeign create(Throwable cause) {
        throw new RuntimeException("远程服务器发生错误,请稍后再次尝试");
    }
}
