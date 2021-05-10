package com.newland.tianyan.face.utils;

import com.newland.tianya.commons.base.constants.GlobalExceptionEnum;
import com.newland.tianya.commons.base.support.ExceptionSupport;
import com.newland.tianya.commons.base.utils.ImageCheckUtils;

import java.io.*;
import java.util.Base64;
import java.util.UUID;
import java.util.regex.Pattern;

/**
 * @Author: huangJunJie  2021-05-10 11:23
 */
public class VideoProcess {

    private static final String VIDEO_STORE_BASEPATH = "D:\\video";

    private final static String BASE64_PATTERN = "^([A-Za-z0-9+/]{4})*([A-Za-z0-9+/]{4}|[A-Za-z0-9+/]{3}=|[A-Za-z0-9+/]{2}==)$";

    /**
     * @param videoFilePath 视频地址
     * @return 返回视频base64编码
     */
    public static String videoToBase64(String videoFilePath) {
        String videoBase64 = null;
        BufferedInputStream bis = null;
        try {
            File videoFile = new File(videoFilePath);
            byte[] videoBytes = new byte[(int) videoFile.length()];
            bis = new BufferedInputStream(new FileInputStream(videoFile));
            bis.read(videoBytes);
            videoBase64 = Base64.getEncoder().encodeToString(videoBytes);
        } catch (FileNotFoundException e) {
            System.out.println("没有找到文件 " + videoFilePath);
        } catch (IOException e) {
            System.out.println("文件读取出错");
        } finally {
            try {
                bis.close();
            } catch (IOException e) {
                System.out.println("关闭输入流出错");
            }
        }
        return videoBase64;
    }

    /**
     * @param videoBase64 视频base编码
     * @return 解码并保存视频，返回视频的保存路径
     */
    public static String base64ToVideoAndSaveVideo(String videoBase64){
        if (!Pattern.matches(BASE64_PATTERN, videoBase64)) {
            System.out.println("不符合base64编码规则");;
        }
        byte[] videoBytes = Base64.getDecoder().decode(videoBase64);
        try {
            String storagePath = VIDEO_STORE_BASEPATH + "\\" + UUID.randomUUID().toString().substring(0, 18)+".mp4";
            BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream(storagePath));
            bos.write(videoBytes, 0, videoBytes.length);
            return storagePath;
        } catch (FileNotFoundException e) {
            System.out.println("文件路径不存在");
        } catch (IOException e) {
            System.out.println("写入出错");
        }
        return null;
    }
}
