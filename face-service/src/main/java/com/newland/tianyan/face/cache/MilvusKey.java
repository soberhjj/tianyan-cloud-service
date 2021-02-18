package com.newland.tianyan.face.cache;

import java.text.NumberFormat;

/**
 * @author: RojiaHuang
 * @description:
 * @date: 2021/2/9
 */
public class MilvusKey {

    private static int allpreLong = 18;

    private static int faceNoLong = 16;

    private static int gpreLong = 8;

    private static Long ALL_PRE = new Double(Math.pow(10, allpreLong)).longValue();

    private static Long GID_PRE = new Double(Math.pow(10, gpreLong)).longValue();

    private static Long FACE_PRE = new Double(Math.pow(10, faceNoLong)).longValue();

    public static Long generatedKey(Long gid, Long uid) {
        return ALL_PRE + randomFaceNo() * FACE_PRE + gid * GID_PRE + uid;
    }

    private static Long randomFaceNo() {
        int max = 100, min = 1;
        return new Double(Math.random() * (max - min) + min).longValue();
    }

    public static Long splitGid(Long key) {
        //giddy8位，uid8位，共占位最长16位,最高位到16位中间有3位用来存储图片的随机标识符确保不冲突
        int pre = allpreLong + 1 - faceNoLong;
        String gidStr = key.toString().substring(pre, gpreLong + pre);
        return Long.valueOf(gidStr);
    }

    public static Long splitUid(Long key) {
//        return key - ALL_PRE - gid * GID_PRE;
        int pre = allpreLong + 1 - gpreLong;
        String uidStr = key.toString().substring(pre, allpreLong + 1);
        return Long.valueOf(uidStr);
    }

    public static void main(String[] args) {
        NumberFormat nf = NumberFormat.getInstance();
//        Long max = Long.MAX_VALUE;
//        Long long19 = new Double(Math.pow(10, 18)).longValue();
//        System.out.println(nf.format(long19));
//        System.out.println(nf.format(max));

        Long beforeUid = 10000008L;
        Long beforeGid = 10000023L;
        System.out.println(nf.format(beforeGid));
        System.out.println(nf.format(beforeUid));

        Long key = MilvusKey.generatedKey(beforeGid, beforeUid);
        System.out.println(nf.format(key));

        Long afterGid = MilvusKey.splitGid(key);
        Long afterUid = MilvusKey.splitUid(key);
        System.out.println(nf.format(afterGid));
        System.out.println(nf.format(afterUid));
    }
}
