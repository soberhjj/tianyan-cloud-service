package com.newland.tianyan.image.service.impl;

import com.github.tobato.fastdfs.domain.fdfs.StorePath;
import com.github.tobato.fastdfs.domain.proto.storage.DownloadByteArray;
import com.github.tobato.fastdfs.service.FastFileStorageClient;
import com.newland.tianyan.common.utils.ImageCheckUtils;
import com.newland.tianyan.image.service.IImageStoreService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import sun.misc.BASE64Decoder;
import sun.misc.BASE64Encoder;

import java.io.ByteArrayInputStream;
import java.io.IOException;

/**
 * @Author: huangJunJie  2021-02-07 09:36
 */
@Service
@Slf4j
public class ImageStorageServiceImpl implements IImageStoreService {

    @Autowired
    private FastFileStorageClient fastFileStorageClient;

    /**
     * 保存为jpg格式
     * @param image
     * @return
     * @throws IOException
     */
    @Override
    public String uploadImage(String image) throws IOException {
        ImageCheckUtils.imageCheck(image);
        BASE64Decoder decoder = new BASE64Decoder();
        byte[] bytes = decoder.decodeBuffer(image);
        StorePath storePath = fastFileStorageClient.uploadFile(new ByteArrayInputStream(bytes), bytes.length, "jpg", null);
        return storePath.getFullPath();
    }

    /**
     * 按图片原格式（jpg、png、bmp）保存
     * @param image
     * @return
     * @throws IOException
     */
    @Override
    public String uploadImageV2(String image) throws IOException {
        ImageCheckUtils.imageCheck(image);
        BASE64Decoder decoder = new BASE64Decoder();
        byte[] bytes = decoder.decodeBuffer(image);
        String imageFormat;
        if (0xFFD8 == ((bytes[0] & 0xff) << 8 | (bytes[1] & 0xff))) {
            imageFormat = "jpg";
        } else if (0x8950 == ((bytes[0] & 0xff) << 8 | (bytes[1] & 0xff))) {
            imageFormat = "png";
        } else {
            imageFormat = "bmp";
        }
        StorePath storePath = fastFileStorageClient.uploadFile(new ByteArrayInputStream(bytes), bytes.length, imageFormat, null);
        return storePath.getFullPath();
    }

    @Override
    public String downloadImage(String imagePath) {
        if (StringUtils.isNotBlank(imagePath)) {
            String group = imagePath.substring(0, imagePath.indexOf("/"));
            String path = imagePath.substring(imagePath.indexOf("/") + 1);
            DownloadByteArray byteArray = new DownloadByteArray();
            byte[] data = fastFileStorageClient.downloadFile(group, path, byteArray);
            BASE64Encoder encoder = new BASE64Encoder();
            String encode = encoder.encode(data);
            return encode.replace("\\r\\n", "");
        } else {
            return null;
        }
    }
}