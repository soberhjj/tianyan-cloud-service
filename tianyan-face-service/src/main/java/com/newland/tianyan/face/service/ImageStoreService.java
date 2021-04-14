package com.newland.tianyan.face.service;

import java.io.IOException;

/**
 * @author: RojiaHuang
 * @description:
 * @date: 2021/4/13
 */
public interface ImageStoreService {

    String download(String image);

    String upload(String image) throws IOException;

    void uploadAsync(String image) throws IOException;
}
