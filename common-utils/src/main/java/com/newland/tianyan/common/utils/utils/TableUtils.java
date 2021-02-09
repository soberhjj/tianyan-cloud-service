package com.newland.tianyan.common.utils.utils;

import org.apache.commons.codec.digest.DigestUtils;

public class TableUtils {

    public static String generateAppTableName(String account) {
        if (account == null) account = "";
        return "APP_INFO_" + DigestUtils.md5Hex(account.getBytes()).substring(8, 24);
    }

    public static String generateGroupTableName(String account, Long appId) {
        if (account == null) account = "";
        String hex = DigestUtils.md5Hex(account);
        return "GROUP_INFO_" + DigestUtils.md5Hex(hex + appId).substring(8, 24);
    }

    public static String generateUserTableName(String account, Long appId, String groupId) {
        if (account == null) account = "";
        if (groupId == null) groupId = "";
        String hex = DigestUtils.md5Hex(account);
        String hex1 = DigestUtils.md5Hex(hex + appId);
        return "USER_INFO_" + DigestUtils.md5Hex(hex1 + groupId).substring(8, 24);
    }

    public static String generateFaceTableName(String account, Long appId/*, String groupId */) {
        if (account == null) account = "";
//        if (groupId == null) groupId = "";
//        int hashCode = groupId.hashCode();
        String hex = DigestUtils.md5Hex(account);
        return "CLOUD_MANAGEMENT_FACE_" + DigestUtils.md5Hex(hex + appId).substring(8, 24)/* + hashCode*/;
    }
}
