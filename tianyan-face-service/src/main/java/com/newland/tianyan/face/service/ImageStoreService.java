package com.newland.tianyan.face.service;

import com.newland.tianya.commons.base.model.imagestrore.DownloadResDTO;
import com.newland.tianya.commons.base.model.imagestrore.UploadResDTO;

import java.io.IOException;
import java.util.List;

/**
 * @author: RojiaHuang
 * @description:
 * @date: 2021/4/13
 */
public interface ImageStoreService {

    List<DownloadResDTO> batchDownload(List<String> images) throws IOException;

    String download(String image);

    List<UploadResDTO> batchUpload(List<String> images) throws IOException;

    UploadResDTO upload(String image) throws IOException;

    void uploadAsync(String image) throws IOException;
}
