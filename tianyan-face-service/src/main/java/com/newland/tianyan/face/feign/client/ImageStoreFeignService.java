package com.newland.tianyan.face.feign.client;



import com.newland.tianyan.commons.webcore.api.IImageStorageApi;
import com.newland.tianyan.face.feign.FeignConfiguration;
import com.newland.tianyan.face.feign.fallback.ImageServiceFeignClientFallbackImpl;
import org.springframework.cloud.openfeign.FeignClient;

/**
 * @author: RojiaHuang
 * @description:
 * @date: 2021/2/3
 */
@FeignClient(name = "image-store-service",
        configuration = FeignConfiguration.class,
        //fallback = ImageStoreFeignFallback.class
        fallbackFactory = ImageServiceFeignClientFallbackImpl.class
)
public interface ImageStoreFeignService extends IImageStorageApi {
}
