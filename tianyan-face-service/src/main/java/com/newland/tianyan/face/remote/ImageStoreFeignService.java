package com.newland.tianyan.face.remote;


import com.newland.tianyan.common.api.IImageStorageApi;
import com.newland.tianyan.face.remote.fallback.ImageServiceFeignClientFallbackImpl;
import org.springframework.cloud.openfeign.FeignClient;

/**
 * @author: RojiaHuang
 * @description:
 * @date: 2021/2/3
 */
@FeignClient(name = "image-store-service", fallbackFactory = ImageServiceFeignClientFallbackImpl.class)
public interface ImageStoreFeignService extends IImageStorageApi {
}
