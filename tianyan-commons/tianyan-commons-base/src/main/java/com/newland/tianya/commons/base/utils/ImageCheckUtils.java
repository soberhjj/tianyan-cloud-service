package com.newland.tianya.commons.base.utils;


import com.newland.tianya.commons.base.constants.GlobalExceptionEnum;
import com.newland.tianya.commons.base.exception.ArgumentException;

import java.util.regex.Pattern;


/**
 * @Author: huangJunJie  2021-02-25 17:51
 */
public class ImageCheckUtils {

    private final static String BASE64_PATTERN = "([A-Za-z0-9+/]{4})*([A-Za-z0-9+/]{3}=|[A-Za-z0-9+/]{2}==)?";
    private final static int MAX_IMAGE_SIZE = 1024 * 1024 * 2;
    private final static String JPG_BASE64_PREFIX = "/9j";
    private final static String PNG_BASE64_PREFIX = "iVBORw0KGgoA";
    private final static String BMP_BASE64_PREFIX = "Qk0";


    public static void imageCheck(String image) {
        String base64 = image.replaceAll("[\\s*\t\n\r]", "");
        if (!Pattern.matches(BASE64_PATTERN, base64)) {
            throw new ArgumentException(GlobalExceptionEnum.BASE64_FORMAT_ILLEGAL.getErrorCode(),
                    GlobalExceptionEnum.BASE64_FORMAT_ILLEGAL.getErrorMsg());
        }
        if ((!image.startsWith(JPG_BASE64_PREFIX)) && (!image.startsWith(PNG_BASE64_PREFIX)) && (!image.startsWith(BMP_BASE64_PREFIX))) {
            throw new ArgumentException(GlobalExceptionEnum.IMAGE_FORMAT_ILLEGAL.getErrorCode(),
                    GlobalExceptionEnum.IMAGE_FORMAT_ILLEGAL.getErrorMsg());
        }
        int length = image.length();
        if (length > MAX_IMAGE_SIZE) {
            throw new ArgumentException(GlobalExceptionEnum.IMAGE_SIZE_OVER_2MB.getErrorCode(),
                    GlobalExceptionEnum.IMAGE_SIZE_OVER_2MB.getErrorMsg());
        }
    }

}
