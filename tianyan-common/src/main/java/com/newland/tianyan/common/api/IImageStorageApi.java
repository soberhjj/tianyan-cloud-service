package com.newland.tianyan.common.api;

import com.newland.tianyan.common.model.imagestrore.DownloadReqDTO;
import com.newland.tianyan.common.model.imagestrore.DownloadResDTO;
import com.newland.tianyan.common.model.imagestrore.UploadReqDTO;
import com.newland.tianyan.common.model.imagestrore.UploadResDTO;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * @author: RojiaHuang
 * @description:
 * @date: 2021/2/20
 */
@RequestMapping("/imagestore/v1")
public interface IImageStorageApi {

    @PostMapping("/upload")
    UploadResDTO upload(@RequestBody UploadReqDTO uploadReq);

    @PostMapping("/uploadV2")
    UploadResDTO uploadV2(@RequestBody UploadReqDTO uploadReq);

    @PostMapping("/download")
    DownloadResDTO download(@RequestBody DownloadReqDTO downloadReq);
}
