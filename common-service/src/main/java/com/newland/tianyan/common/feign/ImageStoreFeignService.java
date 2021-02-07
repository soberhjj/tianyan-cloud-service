package com.newland.tianyan.common.feign;

import com.newland.tianyan.common.feign.falback.FeignConfiguration;
import com.newland.tianyan.common.feign.falback.ImageServiceFeignClientFallbackImpl;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;

/**
 * @author: RojiaHuang
 * @description:
 * @date: 2021/2/3
 */
@FeignClient(name = "image-store-service", fallback = ImageServiceFeignClientFallbackImpl.class,
        configuration = FeignConfiguration.class)
public interface ImageStoreFeignService {

    String uploadImage(String image);

    String downloadImage(String imagePath);

    String uploadImageV2(String image);

}
