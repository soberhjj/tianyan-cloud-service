package com.newland.tianyan.common.api;

import com.newland.tianyan.common.model.imagestrore.DownloadReqDTO;
import com.newland.tianyan.common.model.imagestrore.DownloadResDTO;
import com.newland.tianyan.common.model.imagestrore.UploadReqDTO;
import com.newland.tianyan.common.model.imagestrore.UploadResDTO;
import com.newland.tianyan.common.version.ApiVersion;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

import java.io.IOException;

/**
 * @author: RojiaHuang
 * @description:
 * @date: 2021/2/20
 */
@RequestMapping("/imagestore/v1")
public interface IImageStorageApi {

    @PostMapping("/upload")
    @ApiVersion(1)
    UploadResDTO upload(@RequestBody UploadReqDTO uploadReq) throws IOException;

    @PostMapping("/uploadV2")
    @ApiVersion(1)
    UploadResDTO uploadV2(@RequestBody UploadReqDTO uploadReq) throws IOException;

    @PostMapping("/download")
    @ApiVersion(1)
    DownloadResDTO download(@RequestBody DownloadReqDTO downloadReq);
}
