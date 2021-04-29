package com.newland.tianyan.face.service;

import com.newland.tianya.commons.base.model.imagestrore.UploadResDTO;

import java.io.IOException;

/**
 * @author: RojiaHuang
 * @description:
 * @date: 2021/4/13
 */
public interface ImageStoreService {

    String download(String image);

    UploadResDTO upload(String image) throws IOException;

    void uploadAsync(String image) throws IOException;
}
