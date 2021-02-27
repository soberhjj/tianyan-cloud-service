package com.newland.tianyan.face.feign.fallback;



import com.newland.tianyan.face.feign.ImageStoreFeignService;
import feign.hystrix.FallbackFactory;
import org.springframework.stereotype.Component;

/**
 * @author: RojiaHuang
 * @description: 降级处理
 * @date: 2021/2/2
 */
@Component
public class ImageServiceFeignClientFallbackImpl implements FallbackFactory<ImageStoreFeignService> {

    @Override
    public ImageStoreFeignService create(Throwable throwable) {
        throw new RuntimeException("远程服务器发生错误,请稍后再次尝试");
    }
}