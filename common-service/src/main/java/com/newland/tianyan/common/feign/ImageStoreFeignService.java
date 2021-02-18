package com.newland.tianyan.common.feign;



import com.newland.tianyan.common.feign.dto.image.DownloadReq;
import com.newland.tianyan.common.feign.dto.image.DownloadRes;
import com.newland.tianyan.common.feign.dto.image.UploadReq;
import com.newland.tianyan.common.feign.dto.image.UploadRes;
import com.newland.tianyan.common.feign.falback.ImageServiceFeignClientFallbackImpl;
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
