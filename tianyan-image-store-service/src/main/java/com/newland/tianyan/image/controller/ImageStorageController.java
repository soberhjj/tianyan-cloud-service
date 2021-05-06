package com.newland.tianyan.image.controller;

import com.newland.tianya.commons.base.model.imagestrore.*;
import com.newland.tianyan.commons.webcore.api.IImageStorageApi;
import com.newland.tianyan.image.service.IImageStoreService;
import com.newland.tianyan.image.utils.MD5Util;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.io.IOException;
import java.util.LinkedList;
import java.util.List;

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
    public UploadResDTO upload(@RequestBody @Valid UploadReqDTO uploadReq) throws IOException {
        UploadResDTO res = new UploadResDTO();
        res.setImagePath(imageStorageService.uploadImage(uploadReq.getImage()));
        res.setImageMD5(MD5Util.crypt(uploadReq.getImage()));
        return res;
    }

    @Override
    @PostMapping("/uploadV2")
    public UploadResDTO uploadV2(@RequestBody @Valid  UploadReqDTO uploadReq) throws IOException {
        UploadResDTO res = new UploadResDTO();
        res.setImagePath(imageStorageService.uploadImageV2(uploadReq.getImage()));
        res.setImageMD5(MD5Util.crypt(uploadReq.getImage()));
        return res;
    }

    @Override
    @PostMapping("/download")
    public DownloadResDTO download(@RequestBody @Valid  DownloadReqDTO downloadReq) {
        DownloadResDTO res = new DownloadResDTO();
        res.setImage(imageStorageService.downloadImage(downloadReq.getImagePath()));
        return res;
    }

    @Override
    @PostMapping("/asyncUpload")
    public void asyncUpload(@RequestBody @Valid  UploadReqDTO uploadReq) throws IOException {
        imageStorageService.asyncUploadImage(uploadReq.getImage());
    }

    @Override
    @PostMapping("/batchUpload")
    public List<UploadResDTO> batchUpload(@RequestBody @Valid BatchUploadReqDTO batchUploadReqDTO) throws IOException {
        List<UploadResDTO> res = new LinkedList<>();
        List<String> images = batchUploadReqDTO.getImages();
        for (String image : images) {
            UploadResDTO uploadResDTO = new UploadResDTO();
            uploadResDTO.setImagePath(imageStorageService.uploadImageV2(image));
            uploadResDTO.setImageMD5(MD5Util.crypt(image));
            res.add(uploadResDTO);
        }
        return res;
    }

    @Override
    @PostMapping("/batchDownload")
    public List<DownloadResDTO> batchDownload(@RequestBody @Valid BatchDownloadReqDTO batchDownloadReqDTO) throws IOException {
        List<DownloadResDTO> res = new LinkedList<>();
        List<String> imagesPath = batchDownloadReqDTO.getImagesPath();
        for (String imagePath : imagesPath) {
            DownloadResDTO downloadResDTO = new DownloadResDTO();
            downloadResDTO.setImage(imageStorageService.downloadImage(imagePath));
            res.add(downloadResDTO);
        }
        return res;
    }
}
