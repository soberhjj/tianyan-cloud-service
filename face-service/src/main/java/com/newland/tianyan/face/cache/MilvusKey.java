package com.newland.tianyan.face.cache;

import java.text.NumberFormat;

/**
 * @author: RojiaHuang
 * @description:
 * @date: 2021/2/9
 */
public class MilvusKey {

    private static final int allpreLong = 18;

    private static final int gLong = 8;

    private static final int uLong = 8;

    private static final int fLong = 2;


    public static Long generatedKey(Long gid, Long uid, int faceNoLong) {
        //最高位占位1，giddy8位，uid8位，faceNum 2位
        String key = "1" + lpad(gid, gLong) + lpad(uid, uLong) + lpad(faceNoLong, fLong);
        return Long.parseLong(key);
    }

    public static Long splitGid(Long key) {
        int pre = 1;
        String gidStr = key.toString().substring(pre, gLong + pre);
        return Long.valueOf(gidStr);
    }

    public static Long splitUid(Long key) {
        int pre = allpreLong - 1 - gLong;
        String uidStr = key.toString().substring(pre, allpreLong - fLong + 1);
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
        if (value.toString().length() == maxLength) {
            return value.toString();
        }
        String format = "%0" + maxLength + "d";
        return String.format(format, value);
    }

    public static void main(String[] args) {
        NumberFormat nf = NumberFormat.getInstance();

        Long beforeUid = 10000008L;
        Long beforeGid = 10000023L;
        System.out.println(nf.format(beforeGid));
        System.out.println(nf.format(beforeUid));

        Long key = MilvusKey2.generatedKey(beforeGid, beforeUid, 2);
        System.out.println(nf.format(key));

        Long afterGid = MilvusKey2.splitGid(key);
        Long afterUid = MilvusKey2.splitUid(key);
        System.out.println(nf.format(afterGid));
        System.out.println(nf.format(afterUid));
    }
}
