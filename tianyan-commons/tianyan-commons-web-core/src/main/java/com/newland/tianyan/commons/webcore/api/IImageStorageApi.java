package com.newland.tianyan.commons.webcore.api;

import com.newland.tianya.commons.base.model.imagestrore.DownloadReqDTO;
import com.newland.tianya.commons.base.model.imagestrore.DownloadResDTO;
import com.newland.tianya.commons.base.model.imagestrore.UploadReqDTO;
import com.newland.tianya.commons.base.model.imagestrore.UploadResDTO;
import com.newland.tianyan.commons.webcore.annotation.ApiVersion;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.validation.Valid;
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
    UploadResDTO upload(@RequestBody @Valid UploadReqDTO uploadReq) throws IOException;

    @PostMapping("/uploadV2")
    @ApiVersion(1)
    UploadResDTO uploadV2(@RequestBody @Valid UploadReqDTO uploadReq) throws IOException;

    @PostMapping("/download")
    @ApiVersion(1)
    DownloadResDTO download(@RequestBody @Valid DownloadReqDTO downloadReq);

    @PostMapping("/asyncUpload")
    @ApiVersion(1)
    void asyncUpload(@RequestBody @Valid UploadReqDTO uploadReq) throws IOException;
}
