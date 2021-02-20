package com.newland.tianyan.face.remote;


import com.newland.tianyan.common.model.imageStoreService.IFastdfsImageStorageApi;
import com.newland.tianyan.face.remote.fallback.ImageServiceFeignClientFallbackImpl;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * @author: RojiaHuang
 * @description:
 * @date: 2021/2/3
 */
@FeignClient(name = "image-store-service", fallbackFactory = ImageServiceFeignClientFallbackImpl.class)
@RequestMapping("/backend/image")
public interface ImageStoreFeignService extends IFastdfsImageStorageApi {
}
