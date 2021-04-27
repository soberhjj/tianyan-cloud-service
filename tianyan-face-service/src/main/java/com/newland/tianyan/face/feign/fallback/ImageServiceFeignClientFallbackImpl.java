package com.newland.tianyan.face.feign.fallback;


import com.newland.tianya.commons.base.constants.GlobalExceptionEnum;
import com.newland.tianya.commons.base.model.imagestrore.*;
import com.newland.tianya.commons.base.support.ExceptionSupport;
import com.newland.tianyan.face.feign.client.ImageStoreFeignService;
import feign.hystrix.FallbackFactory;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.validation.Valid;
import java.util.ArrayList;
import java.util.List;

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
        log.warn("tianyan-image-store service failed，cause:{}", throwable.getMessage());
        return new ImageStoreFeignService() {
            @Override
            public UploadResDTO upload(@Valid UploadReqDTO uploadReq) {
                return UploadResDTO.builder().imagePath("fallback").build();
            }

            @Override
            public UploadResDTO uploadV2(@Valid UploadReqDTO uploadReq) {
                //同步方法，出错的话需要抛出异常
                throw ExceptionSupport.toException(GlobalExceptionEnum.SYSTEM_ERROR);
            }

            @Override
            public DownloadResDTO download(@Valid DownloadReqDTO downloadReq) {
                return DownloadResDTO.builder().build();
            }

            @Override
            public void asyncUpload(@Valid UploadReqDTO uploadReq) {
            }

            @Override
            public List<UploadResDTO> batchUpload(@Valid BatchUploadReqDTO batchUploadReqDTO) {
                List<UploadResDTO> fallbackList = new ArrayList<>();
                int size = batchUploadReqDTO.getImages().size();
                for (int i = 0; i < size; i++) {
                    fallbackList.add(UploadResDTO.builder().imagePath("fallback").build());
                }
                return fallbackList;
            }

            @Override
            public List<DownloadResDTO> batchDownload(@Valid BatchDownloadReqDTO batchDownloadReqDTO) {
                List<DownloadResDTO> fallbackList = new ArrayList<>();
                int size = batchDownloadReqDTO.getImagesPath().size();
                for (int i = 0; i < size; i++) {
                    fallbackList.add(DownloadResDTO.builder().image("null").build());
                }
                return fallbackList;
            }
        };
    }
}
