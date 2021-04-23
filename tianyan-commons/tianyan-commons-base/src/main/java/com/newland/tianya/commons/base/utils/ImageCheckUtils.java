package com.newland.tianya.commons.base.utils;


import com.newland.tianya.commons.base.constants.GlobalExceptionEnum;
import com.newland.tianya.commons.base.support.ExceptionSupport;

import java.util.Base64;
import java.util.regex.Pattern;


/**
 * @Author: huangJunJie  2021-02-25 17:51
 */
public class ImageCheckUtils {

    private final static String BASE64_PATTERN = "([A-Za-z0-9+/]{4})*([A-Za-z0-9+/]{3}=|[A-Za-z0-9+/]{2}==)?";
    private final static String BASE64_PATTERN_STRICT = "^([A-Za-z0-9+/]{4})*([A-Za-z0-9+/]{4}|[A-Za-z0-9+/]{3}=|[A-Za-z0-9+/]{2}==)$";
    private final static int MAX_IMAGE_SIZE = 1024 * 1024 * 2;

    public static void imageCheck(String image, boolean isStrict) {
        String pattern = isStrict ? BASE64_PATTERN_STRICT : BASE64_PATTERN;
        if (!Pattern.matches(pattern, image)) {
            throw ExceptionSupport.toException(GlobalExceptionEnum.BASE64_FORMAT_ILLEGAL);
        }

        int length = image.length();
        if (length > MAX_IMAGE_SIZE) {
            throw ExceptionSupport.toException(GlobalExceptionEnum.IMAGE_SIZE_OVER_2MB);
        }

        byte[] b = Base64.getDecoder().decode(image);
        boolean bmp = 0x424D == ((b[0] & 0xff) << 8 | (b[1] & 0xff));
        boolean png = 0x8950 == ((b[0] & 0xff) << 8 | (b[1] & 0xff));
        boolean jpg = 0xFFD8 == ((b[0] & 0xff) << 8 | (b[1] & 0xff));
        if ((!bmp) && (!png) && (!jpg)) {
            throw ExceptionSupport.toException(GlobalExceptionEnum.IMAGE_FORMAT_ILLEGAL);
        }

    }

    public static String imageCheckAndFormatting(String image) {
        String base64 = image.replaceAll("[\\s*\t\n\r]", "");
        imageCheck(base64, true);
        return base64;
    }

}
