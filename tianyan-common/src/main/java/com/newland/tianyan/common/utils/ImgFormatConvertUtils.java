package com.newland.tianyan.common.utils;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Base64;

/**
 * @Author: huangJunJie  2021-02-23 13:49
 */
public class ImgFormatConvertUtils {

    public static String convertToJpg(String imageBase64) {
        //返回值
        String result = imageBase64;
        //base64加解码对象
        final Base64.Decoder decoder = Base64.getDecoder();
        final Base64.Encoder encoder = Base64.getEncoder();
        //图片解码
        byte[] imageByte = decoder.decode(imageBase64);
        //若图片格式为jpg，则不做任何处理
        if (0xFFD8 == ((imageByte[0] & 0xff) << 8 | (imageByte[1] & 0xff))) {
            return result;
        }
        //若图片是否是png或bmp格式,转换为jpg格式
        if (0x8950 == ((imageByte[0] & 0xff) << 8 | (imageByte[1] & 0xff))
                || 0x424D == ((imageByte[0] & 0xff) << 8 | (imageByte[1] & 0xff))) {
            InputStream is = new ByteArrayInputStream(imageByte);
            ByteArrayOutputStream bos = new ByteArrayOutputStream();

            try {
                BufferedImage bufferedImage = ImageIO.read(is);
                BufferedImage jpgBufferedImage = new BufferedImage(bufferedImage.getWidth(), bufferedImage.getHeight(), BufferedImage.TYPE_INT_RGB);
                jpgBufferedImage.createGraphics().drawImage(bufferedImage, 0, 0, Color.WHITE, null);

                ImageIO.write(jpgBufferedImage, "jpg", bos);
                bos.flush();

                //将jpg图片编码为base64
                result = encoder.encodeToString(bos.toByteArray());
                return result;
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        //若不是jpg、png、bmp这三种格式，或者转换为jpg格式时出现异常，返回null
        result = null;
        return result;
    }
}
