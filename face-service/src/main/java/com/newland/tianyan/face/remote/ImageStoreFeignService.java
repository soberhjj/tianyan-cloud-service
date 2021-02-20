package com.newland.tianyan.face.remote;


import com.newland.tianyan.common.model.imageStoreService.dto.DownloadReqDO;
import com.newland.tianyan.common.model.imageStoreService.dto.DownloadResDO;
import com.newland.tianyan.common.model.imageStoreService.dto.UploadReqDO;
import com.newland.tianyan.common.model.imageStoreService.dto.UploadResDO;
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
    UploadResDO uploadImageV2(@RequestBody UploadReqDO uploadReqDO);

    @PostMapping(URI_PREFIX + "/download")
    DownloadResDO downloadImage(@RequestBody DownloadReqDO downloadReqDO);
}
