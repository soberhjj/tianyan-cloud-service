package com.newland.tianyan.common.feign.falback;

import com.newland.tianyan.common.feign.ImageStoreFeignService;
import org.springframework.stereotype.Component;

/**
 * @author: RojiaHuang
 * @description: 降级处理
 * @date: 2021/2/2
 */
@Component
public class ImageServiceFeignClientFallbackImpl implements ImageStoreFeignService {
    @Override
    public String uploadImage(String image) {
        return null;
    }

    @Override
    public String downloadImage(String imagePath) {
        return null;
    }

    @Override
    public String uploadImageV2(String image) {
        return null;
    }
}
