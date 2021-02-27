package com.newland.tianyan.image.service;

import java.io.IOException;

/**
 * @author: RojiaHuang
 * @description:
 * @date: 2021/2/23
 */
public interface IImageStoreService {

    String uploadImage(String image) throws IOException;

    String uploadImageV2(String image) throws IOException;

    String downloadImage(String imagePath);

}
