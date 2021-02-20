package com.newland.tianyan.common.utils.utils;

public class AppUtils {

    public static long generateAppId() {
        return System.currentTimeMillis();
    }

    public static String generateApiKey() {
        return new RandomString(20).nextString();
    }

    public static String generateSecretKey() {
        return new RandomString(32).nextString();
    }
}
