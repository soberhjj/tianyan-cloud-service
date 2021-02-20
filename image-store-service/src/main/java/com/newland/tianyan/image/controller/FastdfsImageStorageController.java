package com.newland.tianyan.image.controller;

import com.newland.tianyan.common.model.imageStoreService.IFastdfsImageStorageApi;
import com.newland.tianyan.common.model.imageStoreService.dto.DownloadReqDTO;
import com.newland.tianyan.common.model.imageStoreService.dto.DownloadResDTO;
import com.newland.tianyan.common.model.imageStoreService.dto.UploadReqDTO;
import com.newland.tianyan.common.model.imageStoreService.dto.UploadResDTO;
import com.newland.tianyan.image.service.FastdfsImageStorageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @Author: huangJunJie  2021-02-07 10:07
 */
@RestController
@RequestMapping("/backend/image")
public class FastdfsImageStorageController implements IFastdfsImageStorageApi {

    @Autowired
    private FastdfsImageStorageService fastdfsImageStorageService;

    @Override
    @PostMapping("/upload")
    public UploadResDTO upload(@RequestBody UploadReqDTO uploadReq) {
        UploadResDTO res = new UploadResDTO();
        res.setImagePath(fastdfsImageStorageService.uploadImage(uploadReq.getImage()));
        return res;
    }

    @Override
    @PostMapping("/uploadV2")
    public UploadResDTO uploadV2(@RequestBody UploadReqDTO uploadReq) {
        UploadResDTO res = new UploadResDTO();
        res.setImagePath(fastdfsImageStorageService.uploadImageV2(uploadReq.getImage()));
        return res;
    }

    @Override
    @PostMapping("/download")
    public DownloadResDTO download(@RequestBody DownloadReqDTO downloadReq) {
        DownloadResDTO res=new DownloadResDTO();
        res.setImage(fastdfsImageStorageService.downloadImage(downloadReq.getImagePath()));
        return res;
    }

}
