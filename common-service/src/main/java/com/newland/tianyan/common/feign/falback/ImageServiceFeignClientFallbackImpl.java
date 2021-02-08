package com.newland.tianyan.common.feign.falback;

import com.newland.tianyan.common.feign.ImageStoreFeignService;
import com.newland.tianyan.common.feign.dto.image.DownloadReq;
import com.newland.tianyan.common.feign.dto.image.DownloadRes;
import com.newland.tianyan.common.feign.dto.image.UploadReq;
import com.newland.tianyan.common.feign.dto.image.UploadRes;
import org.springframework.stereotype.Component;

/**
 * @author: RojiaHuang
 * @description: 降级处理
 * @date: 2021/2/2
 */
@Component
public class ImageServiceFeignClientFallbackImpl implements ImageStoreFeignService {

    @Override
    public UploadRes uploadV2(UploadReq uploadReq) {
        return null;
    }

    @Override
    public DownloadRes download(DownloadReq downloadReq) {
        return null;
    }
}
