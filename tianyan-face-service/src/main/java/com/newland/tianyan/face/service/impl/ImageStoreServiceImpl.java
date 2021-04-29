package com.newland.tianyan.face.service.impl;

import com.newland.tianya.commons.base.model.imagestrore.*;
import com.newland.tianyan.face.feign.client.ImageStoreFeignService;
import com.newland.tianyan.face.service.ImageStoreService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.List;

/**
 * @author: RojiaHuang
 * @description:
 * @date: 2021/4/13
 */
@Service
@Slf4j
public class ImageStoreServiceImpl implements ImageStoreService {

    @Autowired
    private ImageStoreFeignService imageStorageService;

    @Override
    public List<DownloadResDTO> batchDownload(List<String> images) throws IOException {
        BatchDownloadReqDTO batchDownloadReqDTO = BatchDownloadReqDTO.builder().imagesPath(images).build();
        return imageStorageService.batchDownload(batchDownloadReqDTO);
    }

    @Override
    public String download(String image) {
        DownloadResDTO resDTO = imageStorageService.download(DownloadReqDTO.builder().imagePath(image).build());
        // maybe null
        return resDTO.getImage();
    }

    @Override
    public List<UploadResDTO> batchUpload(List<String> images) throws IOException {
        BatchUploadReqDTO batchUploadReqDTO = BatchUploadReqDTO.builder().images(images).build();
        return imageStorageService.batchUpload(batchUploadReqDTO);
    }

    @Override
    public UploadResDTO upload(String image) throws IOException {
        return imageStorageService.uploadV2(UploadReqDTO.builder().image(image).build());
    }

    @Override
    @Async("asyncPool")
    public void uploadAsync(String image) throws IOException {
        log.debug("current-thread:{}", Thread.currentThread().getName());
        imageStorageService.asyncUpload(UploadReqDTO.builder().image(image).build());
    }
}
