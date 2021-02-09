package com.newland.tianyan.common.utils.utils;


import com.newland.tianyan.common.utils.exception.CommonException;
import com.newland.tianyan.common.utils.exception.EmptyImageException;
import com.newland.tianyan.common.utils.exception.ErrorImageSizeException;
import com.newland.tianyan.common.utils.exception.ImageFormatErrorException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import sun.misc.BASE64Decoder;
import sun.misc.BASE64Encoder;

import java.io.*;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.UUID;
import java.util.regex.Pattern;

/**
 * @author newland
 */
@Configuration
public class ImageProcess {

    /** 用户的当前工作目录**/
    private static String LOCAL_FILE_PATH;

    @Value("${local-path:#{'/data3/aike/image/'}}")
    public void setLocalPath(String path) {
        ImageProcess.LOCAL_FILE_PATH = path;
    }

    private final static String BASE64_PATTERN = "([A-Za-z0-9+/]{4})*([A-Za-z0-9+/]{3}=|[A-Za-z0-9+/]{2}==)?";
    private final static int MAX_IMAGE_SIZE = 1024 * 1024 * 2;
    private final static String IMAGE_BASE64_PREFIX = "/9j/";

    private static void imageCheck(String image) {
        int length = image.length();
        if (length > MAX_IMAGE_SIZE) {
            throw new ErrorImageSizeException();
        }
        if (!image.startsWith(IMAGE_BASE64_PREFIX)) {
            throw new ImageFormatErrorException();
        }
    }

    public static String sendPicture(String image, String path, String id) {
        imageCheck(image);
        BASE64Decoder decoder = new BASE64Decoder();
        byte[] bytes;
        try {
            bytes = decoder.decodeBuffer(image);
        } catch (IOException e) {
            e.printStackTrace();
            throw new EmptyImageException();
        }

        image = image.replaceAll("[\\s*\t\n\r]", "");

        if (!Pattern.matches(BASE64_PATTERN, image)) {
            throw new EmptyImageException();
        }
        SimpleDateFormat df1 = new SimpleDateFormat("yyyy-MM-dd");
        String date1 = df1.format(new Date());
        String imagePath = LOCAL_FILE_PATH + "/" + path + "/" + date1 + "/";
        new File(imagePath).mkdirs();
        try {
            OutputStream out = new FileOutputStream(imagePath + id);
            out.write(bytes);
            out.flush();
            out.close();
            return path+"/"+date1+"/";
        } catch (IOException e) {
            throw new CommonException(6400, "file parse exception");
        }
    }

    public static String generateImage(String imgStr) {
        imageCheck(imgStr);
        BASE64Decoder decoder = new BASE64Decoder();
        try {
            byte[] b = decoder.decodeBuffer(imgStr);
            String filename = UUID.randomUUID() + ".jpg";
            OutputStream out = new FileOutputStream(LOCAL_FILE_PATH + filename);
            out.write(b);
            out.flush();
            out.close();
            return filename;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public static String getImageBase64(String filename) {
        InputStream inputStream;
        byte[] data;
        try {
            inputStream = new FileInputStream(LOCAL_FILE_PATH + filename);
            data = new byte[inputStream.available()];
            inputStream.read(data);
            inputStream.close();
        } catch (IOException e) {
            return "null";
        }
        // 加密
        BASE64Encoder encoder = new BASE64Encoder();
        String encode = encoder.encode(data);
        return encode.replace("\\r\\n", "");
    }



}
