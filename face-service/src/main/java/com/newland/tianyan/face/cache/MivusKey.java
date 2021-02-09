package com.newland.tianyan.face.cache;

/**
 * @author: RojiaHuang
 * @description:
 * @date: 2021/2/9
 */
public class MivusKey {

    private static int gpreLong = 18;

    private static int upreLong = 9;

    private static Long GID_PRE = new Double(Math.pow(10, gpreLong)).longValue();

    private static Long UID_PRE = new Double(Math.pow(10, upreLong)).longValue();

    public static Long generaredKey(Long gid,Long uid){
        return  GID_PRE + gid * UID_PRE + uid;
    }

    public static Long splitGid(Long key){
        String gidStr = key.toString().substring(1, upreLong+1);
         return Long.valueOf(gidStr);
    }

    public static Long splitUid(Long key,Long gid){
        return key - GID_PRE - gid * UID_PRE;
    }
}
