package com.newland.tianya.commons.base.utils;


import com.newland.tianya.commons.base.constants.GlobalExceptionEnum;
import com.newland.tianya.commons.base.support.ExceptionSupport;

import java.util.regex.Pattern;


/**
 * @Author: huangJunJie  2021-02-25 17:51
 */
public class ImageCheckUtils {

    private final static String BASE64_PATTERN = "([A-Za-z0-9+/]{4})*([A-Za-z0-9+/]{3}=|[A-Za-z0-9+/]{2}==)?";
    private final static String BASE64_PATTERN_STRICT = "^([A-Za-z0-9+/]{4})*([A-Za-z0-9+/]{4}|[A-Za-z0-9+/]{3}=|[A-Za-z0-9+/]{2}==)$";
    private final static int MAX_IMAGE_SIZE = 1024 * 1024 * 2;
    private final static String JPG_BASE64_PREFIX = "/9j";
    private final static String PNG_BASE64_PREFIX = "iVBORw0KGgoA";
    private final static String BMP_BASE64_PREFIX = "Qk";


    public static void imageCheck(String image,boolean isStrict) {
        String pattern = isStrict? BASE64_PATTERN_STRICT: BASE64_PATTERN;
        if (!Pattern.matches(pattern, image)) {
            throw ExceptionSupport.toException(GlobalExceptionEnum.BASE64_FORMAT_ILLEGAL);
        }
        if ((!image.startsWith(JPG_BASE64_PREFIX)) && (!image.startsWith(PNG_BASE64_PREFIX)) && (!image.startsWith(BMP_BASE64_PREFIX))) {
            throw ExceptionSupport.toException(GlobalExceptionEnum.IMAGE_FORMAT_ILLEGAL);
        }

        int length = image.length();
        if (length > MAX_IMAGE_SIZE) {
            throw ExceptionSupport.toException(GlobalExceptionEnum.IMAGE_SIZE_OVER_2MB);
        }
    }


    public static String imageCheckAndFormatting(String image){
        String base64 = image.replaceAll("[\\s*\t\n\r]", "");
        imageCheck(base64,true);
        return base64;
    }

}
