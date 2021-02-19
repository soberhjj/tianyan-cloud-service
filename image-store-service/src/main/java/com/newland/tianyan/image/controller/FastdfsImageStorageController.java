package com.newland.tianyan.image.controller;

import com.newland.tianyan.image.vo.DownloadReq;
import com.newland.tianyan.image.vo.DownloadRes;
import com.newland.tianyan.image.vo.UploadReq;
import com.newland.tianyan.image.vo.UploadRes;
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
public class FastdfsImageStorageController {

    @Autowired
    FastdfsImageStorageService fastdfsImageStorageService;

    @PostMapping("/upload")
    public UploadRes upload(@RequestBody UploadReq uploadReq) {
        UploadRes res = new UploadRes();
        res.setImagePath(fastdfsImageStorageService.uploadImage(uploadReq.getImage()));
        return res;
    }

    @PostMapping("/uploadV2")
    public UploadRes uploadV2(@RequestBody UploadReq uploadReq) {
        UploadRes res = new UploadRes();
        res.setImagePath(fastdfsImageStorageService.uploadImageV2(uploadReq.getImage()));
        return res;
    }

    @PostMapping("/download")
    public DownloadRes download(@RequestBody DownloadReq downloadReq) {
        DownloadRes res=new DownloadRes();
        res.setImage(fastdfsImageStorageService.downloadImage(downloadReq.getImagePath()));
        return res;
    }
}
