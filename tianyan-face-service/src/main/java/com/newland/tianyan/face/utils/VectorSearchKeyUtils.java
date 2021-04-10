package com.newland.tianyan.face.utils;

import com.newland.tianya.commons.base.exception.BusinessException;
import com.newland.tianyan.face.constant.ExceptionEnum;

import java.text.NumberFormat;

import static com.newland.tianyan.face.constant.BusinessArgumentConstants.MAX_FACE_NUMBER;

/**
 * @author: RojiaHuang
 * @description:
 * @date: 2021/2/9
 */
public class VectorSearchKeyUtils {

    private static final int ALLPRE_LONG = 18;

    private static final int G_LONG = 8;

    private static final int U_LONG = 8;

    private static final int F_LONG = 2;

    public static Long generatedKey(Long gid, Long uid, int faceNoLong) {
        if (faceNoLong > MAX_FACE_NUMBER) {
            throw new BusinessException(ExceptionEnum.OVER_FACE_MAX_NUMBER.getErrorCode(),
                    ExceptionEnum.OVER_FACE_MAX_NUMBER.getErrorMsg());
        }
        //最高位占位1，giddy8位，uid8位，faceNum 2位
        String key = "1" + lpad(gid, G_LONG) + lpad(uid, U_LONG) + lpad(faceNoLong, F_LONG);
        return Long.parseLong(key);
    }

    public static Long splitGid(Long key) {
        int pre = 1;
        String gidStr = key.toString().substring(pre, G_LONG + pre);
        return Long.valueOf(gidStr);
    }

    public static Long splitUid(Long key) {
        int pre = ALLPRE_LONG - 1 - G_LONG;
        String uidStr = key.toString().substring(pre, ALLPRE_LONG - F_LONG + 1);
        return Long.valueOf(uidStr);
    }

    public static String lpad(Long value, int maxLength) {
        if (value.toString().length() == maxLength) {
            return value.toString();
        }
        String format = "%0" + maxLength + "d";
        return String.format(format, value);
    }

    public static String lpad(Integer value, int maxLength) {
        return lpad(new Long(value), maxLength);
    }

    public static void main(String[] args) {
        NumberFormat nf = NumberFormat.getInstance();

//        Long beforeUid = 10000008L;
//        Long beforeGid = 10000023L;
//
//        Long key = VectorSearchKeyUtils.generatedKey(beforeGid, beforeUid, 0);
//        System.out.println(nf.format(key));

//        Long afterGid = VectorSearchKeyUtils.splitGid(key);
//        Long afterUid = VectorSearchKeyUtils.splitUid(key);
//        System.out.println(nf.format(afterGid));
//        System.out.println(nf.format(afterUid));
//        List<Long> keys = Arrays.asList(1100000081000002300L,1100000081000002300L,1100000081000002400L);
//        keys = VectorSearchKeyUtils.filterSameGroupSameUser(keys);
//        keys.forEach(System.out::println);
    }
}
