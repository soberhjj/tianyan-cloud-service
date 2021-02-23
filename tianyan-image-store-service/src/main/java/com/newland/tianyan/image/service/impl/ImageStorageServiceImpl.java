package com.newland.tianyan.image.service.impl;

import com.github.tobato.fastdfs.domain.fdfs.StorePath;
import com.github.tobato.fastdfs.domain.proto.storage.DownloadByteArray;
import com.github.tobato.fastdfs.service.FastFileStorageClient;
import com.newland.tianyan.image.service.IImageStoreService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import sun.misc.BASE64Decoder;
import sun.misc.BASE64Encoder;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.regex.Pattern;

/**
 * @Author: huangJunJie  2021-02-07 09:36
 */
@Service
public class ImageStorageServiceImpl implements IImageStoreService {

    private final String base64Pattern = "([A-Za-z0-9+/]{4})*([A-Za-z0-9+/]{3}=|[A-Za-z0-9+/]{2}==)?";
    private final int maxImageSize = 1024 * 1024 * 2;
    private final String imageBase64Prefix = "/9j/";

    @Autowired
    private FastFileStorageClient fastFileStorageClient;

    @Override
    public String uploadImage(String image) {
        imageCheck(image);
        BASE64Decoder decoder = new BASE64Decoder();
        byte[] bytes = null;
        try {
            bytes = decoder.decodeBuffer(image);
        } catch (IOException e) {
            e.printStackTrace();
            //todo 抛异常
        }
        image = image.replaceAll("[\\s*\t\n\r]", "");
        if (!Pattern.matches(base64Pattern, image)) {
            //todo 抛异常
        }

        StorePath storePath = null;
        if (bytes != null) {
            storePath = fastFileStorageClient.uploadFile(new ByteArrayInputStream(bytes), bytes.length, "jpg", null);
        }
        return storePath.getFullPath();
    }

    /**
     * 上面的uploadImage方法仅支持jpg图片的上传，在上传之前会校验是否是jpg图片，如果不是jpg图片则抛出异场
     * 下面的uploadImageV2方法支持jpg和png这两种格式的图片的上传
     */
    @Override
    public String uploadImageV2(String image) {
        //图片大小检查
        int length = image.length();
        if (length > maxImageSize) {
            //todo 抛异常
        }
        //将图片解码
        BASE64Decoder decoder = new BASE64Decoder();
        byte[] bytes = null;
        try {
            bytes = decoder.decodeBuffer(image);
        } catch (IOException e) {
            e.printStackTrace();
            //todo 抛异常
        }

        //base64串检查
        image = image.replaceAll("[\\s*\t\n\r]", "");
        if (!Pattern.matches(base64Pattern, image)) {
            //todo 抛异常
        }

        String imageFormat = "";
        //判断图片是否是png格式，不是png格式则为jpg格式
        if (0x8950 == ((bytes[0] & 0xff) << 8 | (bytes[1] & 0xff))) {
            imageFormat = "png";
        } else {
            imageFormat = "jpg";
        }

        StorePath storePath = null;
        if (bytes != null) {
            storePath = fastFileStorageClient.uploadFile(new ByteArrayInputStream(bytes), bytes.length, imageFormat, null);
        }
        return storePath.getFullPath();
    }

    @Override
    public String downloadImage(String imagePath) {
        if (StringUtils.isNotBlank(imagePath)) {
            String group = imagePath.substring(0, imagePath.indexOf("/"));
            String path = imagePath.substring(imagePath.indexOf("/") + 1);
            DownloadByteArray byteArray = new DownloadByteArray();
            byte[] data = fastFileStorageClient.downloadFile(group, path, byteArray);

            //转成base64
            BASE64Encoder encoder = new BASE64Encoder();
            String encode = encoder.encode(data);
            return encode.replace("\\r\\n", "");
        } else {
            return null;
        }
    }

    private void imageCheck(String image) {
        int length = image.length();
        if (length > maxImageSize) {
            //todo 抛异常
        }
        if (!image.startsWith(imageBase64Prefix)) {
            //todo 抛异常
        }
    }

}
