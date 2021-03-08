package com.newland.tianyan.image.service;

import java.io.IOException;
import java.util.concurrent.Future;

/**
 * @author: RojiaHuang
 * @description:
 * @date: 2021/2/23
 */
public interface IImageStoreService {

    String uploadImage(String image) throws IOException;

    String uploadImageV2(String image) throws IOException;

    String downloadImage(String imagePath);

    Future<String> asyncUploadImage(String image) throws IOException;
}
