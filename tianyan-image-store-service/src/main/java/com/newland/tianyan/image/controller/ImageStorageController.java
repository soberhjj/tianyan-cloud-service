package com.newland.tianyan.image.controller;

import com.newland.tianyan.common.api.IImageStorageApi;
import com.newland.tianyan.common.exception.BaseException;
import com.newland.tianyan.common.model.imagestrore.DownloadReqDTO;
import com.newland.tianyan.common.model.imagestrore.DownloadResDTO;
import com.newland.tianyan.common.model.imagestrore.UploadReqDTO;
import com.newland.tianyan.common.model.imagestrore.UploadResDTO;
import com.newland.tianyan.image.service.IImageStoreService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;

/**
 * @Author: huangJunJie  2021-02-07 10:07
 */
@RestController
@Slf4j
public class ImageStorageController implements IImageStorageApi {

    @Autowired
    private IImageStoreService imageStorageService;

    @Override
    @PostMapping("/upload")
    public UploadResDTO upload(@RequestBody UploadReqDTO uploadReq) throws IOException {
        log.info("我是业务日志{}","123");
        UploadResDTO res = new UploadResDTO();
        res.setImagePath(imageStorageService.uploadImage(uploadReq.getImage()));
        return res;
    }

    @Override
    @PostMapping("/uploadV2")
    public UploadResDTO uploadV2(@RequestBody UploadReqDTO uploadReq) throws IOException {
        UploadResDTO res = new UploadResDTO();
        res.setImagePath(imageStorageService.uploadImageV2(uploadReq.getImage()));
        return res;
    }

    @Override
    @PostMapping("/download")
    public DownloadResDTO download(@RequestBody DownloadReqDTO downloadReq) {
        DownloadResDTO res = new DownloadResDTO();
        res.setImage(imageStorageService.downloadImage(downloadReq.getImagePath()));
        return res;
    }

    @Override
    @PostMapping("/asyncUpload")
    public void asyncUpload(@RequestBody UploadReqDTO uploadReq) throws IOException {
        imageStorageService.asyncUploadImage(uploadReq.getImage());
    }

}
