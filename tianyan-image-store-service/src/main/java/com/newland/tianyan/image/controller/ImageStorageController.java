package com.newland.tianyan.image.controller;

import com.newland.tianyan.common.api.IImageStorageApi;
import com.newland.tianyan.common.model.imagestrore.DownloadReqDTO;
import com.newland.tianyan.common.model.imagestrore.DownloadResDTO;
import com.newland.tianyan.common.model.imagestrore.UploadReqDTO;
import com.newland.tianyan.common.model.imagestrore.UploadResDTO;
import com.newland.tianyan.image.service.impl.ImageStorageServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

/**
 * @Author: huangJunJie  2021-02-07 10:07
 */
@RestController
public class ImageStorageController implements IImageStorageApi {

    @Autowired
    private ImageStorageServiceImpl imageStorageServiceImpl;

    @Override
    @PostMapping("/upload")
    public UploadResDTO upload(@RequestBody UploadReqDTO uploadReq) {
        UploadResDTO res = new UploadResDTO();
        res.setImagePath(imageStorageServiceImpl.uploadImage(uploadReq.getImage()));
        return res;
    }

    @Override
    @PostMapping("/uploadV2")
    public UploadResDTO uploadV2(@RequestBody UploadReqDTO uploadReq) {
        UploadResDTO res = new UploadResDTO();
        res.setImagePath(imageStorageServiceImpl.uploadImageV2(uploadReq.getImage()));
        return res;
    }

    @Override
    @PostMapping("/download")
    public DownloadResDTO download(@RequestBody DownloadReqDTO downloadReq) {
        DownloadResDTO res = new DownloadResDTO();
        res.setImage(imageStorageServiceImpl.downloadImage(downloadReq.getImagePath()));
        return res;
    }

}
