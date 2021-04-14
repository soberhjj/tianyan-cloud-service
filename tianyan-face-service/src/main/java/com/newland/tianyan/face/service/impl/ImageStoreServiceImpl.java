package com.newland.tianyan.face.service.impl;

import com.newland.tianya.commons.base.model.imagestrore.DownloadReqDTO;
import com.newland.tianya.commons.base.model.imagestrore.DownloadResDTO;
import com.newland.tianya.commons.base.model.imagestrore.UploadReqDTO;
import com.newland.tianya.commons.base.model.imagestrore.UploadResDTO;
import com.newland.tianyan.face.feign.client.ImageStoreFeignService;
import com.newland.tianyan.face.service.ImageStoreService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.io.IOException;

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
    public String download(String image) {
        DownloadResDTO resDTO = imageStorageService.download(DownloadReqDTO.builder().imagePath(image).build());
        // maybe null
        return resDTO.getImage();
    }

    @Override
    public String upload(String image) throws IOException {
        UploadResDTO uploadResDTO = imageStorageService.uploadV2(UploadReqDTO.builder().image(image).build());
        return uploadResDTO.getImagePath();
    }

    @Override
    @Async("asyncPool")
    public void uploadAsync(String image) throws IOException {
        log.debug("current-thread:{}",Thread.currentThread().getName());
        imageStorageService.asyncUpload(UploadReqDTO.builder().image(image).build());
    }
}
