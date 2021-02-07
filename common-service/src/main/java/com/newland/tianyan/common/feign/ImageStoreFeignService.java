package com.newland.tianyan.common.feign;

import com.newland.tianyan.common.feign.dto.imageStore.DownloadReq;
import com.newland.tianyan.common.feign.dto.imageStore.DownloadRes;
import com.newland.tianyan.common.feign.dto.imageStore.UploadReq;
import com.newland.tianyan.common.feign.dto.imageStore.UploadRes;
import com.newland.tianyan.common.feign.falback.FeignConfiguration;
import com.newland.tianyan.common.feign.falback.ImageServiceFeignClientFallbackImpl;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

/**
 * @author: RojiaHuang
 * @description:
 * @date: 2021/2/3
 */
@FeignClient(name = "image-store-service", fallback = ImageServiceFeignClientFallbackImpl.class,
        configuration = FeignConfiguration.class)
public interface ImageStoreFeignService {
    String URI_PREFIX = "/backend/image";

    @PostMapping(URI_PREFIX + "/uploadV2")
    UploadRes uploadV2(@RequestBody UploadReq uploadReq);

    @PostMapping(URI_PREFIX + "/download")
    DownloadRes download(@RequestBody DownloadReq downloadReq);
}
