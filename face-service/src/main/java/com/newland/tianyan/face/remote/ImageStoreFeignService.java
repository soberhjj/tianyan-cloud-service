package com.newland.tianyan.face.remote;


import com.newland.tianyan.common.model.imageStoreService.DownloadReq;
import com.newland.tianyan.common.model.imageStoreService.DownloadRes;
import com.newland.tianyan.common.model.imageStoreService.UploadReq;
import com.newland.tianyan.common.model.imageStoreService.UploadRes;
import com.newland.tianyan.face.remote.fallback.ImageServiceFeignClientFallbackImpl;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

/**
 * @author: RojiaHuang
 * @description:
 * @date: 2021/2/3
 */
@FeignClient(name = "image-store-service", fallbackFactory = ImageServiceFeignClientFallbackImpl.class)
public interface ImageStoreFeignService {
    String URI_PREFIX = "/backend/image";

    @PostMapping(URI_PREFIX + "/uploadV2")
    UploadRes uploadImageV2(@RequestBody UploadReq uploadReq);

    @PostMapping(URI_PREFIX + "/download")
    DownloadRes downloadImage(@RequestBody DownloadReq downloadReq);
}
