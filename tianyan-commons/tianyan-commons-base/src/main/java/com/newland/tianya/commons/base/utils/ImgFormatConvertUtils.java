package com.newland.tianya.commons.base.utils;


import com.newland.tianya.commons.base.constants.ImageTypeEnums;

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

    /**
     * @param imageBase64 图片base64编码串
     * @return 返回JPG格式的图片base64编码串
     * 若转换异常或原图格式非JPG、PNG、BMP这三种格式，则返回null
     */
    public static String convertToJpg(String imageBase64) throws IOException {
        ImageCheckUtils.imageCheck(imageBase64);
        //返回值
        String result = imageBase64;
        //base64加解码对象
        final Base64.Decoder decoder = Base64.getDecoder();
        final Base64.Encoder encoder = Base64.getEncoder();
        //图片解码
        byte[] imageByte = decoder.decode(imageBase64);
        //若图片格式为jpg，不做任何处理，非jpg则将其转为jpg
        if (ImageTypeEnums.JPG.getRule() == ((imageByte[0] & 0xff) << 8 | (imageByte[1] & 0xff))) {
            return result;
        } else {
            InputStream is = new ByteArrayInputStream(imageByte);
            ByteArrayOutputStream bos = new ByteArrayOutputStream();

            BufferedImage bufferedImage = ImageIO.read(is);
            BufferedImage jpgBufferedImage = new BufferedImage(bufferedImage.getWidth(), bufferedImage.getHeight(), BufferedImage.TYPE_INT_RGB);
            jpgBufferedImage.createGraphics().drawImage(bufferedImage, 0, 0, Color.WHITE, null);

            ImageIO.write(jpgBufferedImage, "jpg", bos);
            bos.flush();

            //将jpg图片编码为base64
            result = encoder.encodeToString(bos.toByteArray());
            return result;
        }
    }
}
