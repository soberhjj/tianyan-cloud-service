package com.newland.tianyan.image.service;

/**
 * @author: RojiaHuang
 * @description:
 * @date: 2021/2/23
 */
public interface IImageStoreService {

    String uploadImage(String image);

    String uploadImageV2(String image);

    String downloadImage(String imagePath);
}
