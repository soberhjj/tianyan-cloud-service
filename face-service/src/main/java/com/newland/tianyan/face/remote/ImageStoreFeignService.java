package com.newland.tianyan.face.remote;


import com.newland.tianyan.common.model.imageStoreService.dto.DownloadReqDTO;
import com.newland.tianyan.common.model.imageStoreService.dto.DownloadResDTO;
import com.newland.tianyan.common.model.imageStoreService.dto.UploadReqDTO;
import com.newland.tianyan.common.model.imageStoreService.dto.UploadResDTO;
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
    UploadResDTO uploadImageV2(@RequestBody UploadReqDTO uploadReqDTO);

    @PostMapping(URI_PREFIX + "/download")
    DownloadResDTO downloadImage(@RequestBody DownloadReqDTO downloadReqDTO);
}
