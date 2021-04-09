package com.newland.tianyan.face.feign.fallback;



import com.newland.tianya.commons.base.constants.GlobalExceptionEnum;
import com.newland.tianya.commons.base.model.imagestrore.DownloadReqDTO;
import com.newland.tianya.commons.base.model.imagestrore.DownloadResDTO;
import com.newland.tianya.commons.base.model.imagestrore.UploadReqDTO;
import com.newland.tianya.commons.base.model.imagestrore.UploadResDTO;
import com.newland.tianya.commons.base.support.ExceptionSupport;
import com.newland.tianyan.face.constant.ExceptionEnum;
import com.newland.tianyan.face.feign.client.ImageStoreFeignService;
import feign.hystrix.FallbackFactory;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.validation.Valid;
import java.io.IOException;

/**
 * @author: RojiaHuang
 * @description: 降级处理
 * @date: 2021/2/2
 */
@Component
@Slf4j
public class ImageServiceFeignClientFallbackImpl implements FallbackFactory<ImageStoreFeignService> {

    @Override
    public ImageStoreFeignService create(Throwable throwable) {
        log.warn("tianyan-image-store service failed，cause:{}",throwable.getMessage());
        return new ImageStoreFeignService() {
            @Override
            public UploadResDTO upload(@Valid UploadReqDTO uploadReq) throws IOException {
                return UploadResDTO.builder().imagePath("fallback").build();
            }

            @Override
            public UploadResDTO uploadV2(@Valid UploadReqDTO uploadReq) throws IOException {
                throw  ExceptionSupport.toException(GlobalExceptionEnum.SERVICE_NOT_SUPPORT);
            }

            @Override
            public DownloadResDTO download(@Valid DownloadReqDTO downloadReq) {
                return DownloadResDTO.builder().image(null).build();
            }

            @Override
            public void asyncUpload(@Valid UploadReqDTO uploadReq) throws IOException {
            }
        };
    }
}
