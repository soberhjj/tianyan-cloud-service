package com.newland.tianyan.common.model.imagestoreservice;

import com.newland.tianyan.common.model.imagestoreservice.dto.DownloadReqDTO;
import com.newland.tianyan.common.model.imagestoreservice.dto.DownloadResDTO;
import com.newland.tianyan.common.model.imagestoreservice.dto.UploadReqDTO;
import com.newland.tianyan.common.model.imagestoreservice.dto.UploadResDTO;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * @author: RojiaHuang
 * @description:
 * @date: 2021/2/20
 */
@RequestMapping("/backend/image")
public interface IFastdfsImageStorageApi {

    @PostMapping("/upload")
    UploadResDTO upload(@RequestBody UploadReqDTO uploadReq);


    @PostMapping("/uploadV2")
    UploadResDTO uploadV2(@RequestBody UploadReqDTO uploadReq);

    @PostMapping("/download")
    DownloadResDTO download(@RequestBody DownloadReqDTO downloadReq);
}
