package com.newland.tianyan.commons.webcore.utils;

import javax.servlet.http.HttpServletRequest;

/**
 * @author: RojiaHuang
 * @description:
 * @date: 2021/2/25
 */
public class NetworkUtils {
    private static final String[] HEADERS_TO_TRY = {
            "X-Forwarded-For",
            "Proxy-Client-IP",
            "WL-Proxy-Client-IP",
            "HTTP_X_FORWARDED_FOR",
            "HTTP_X_FORWARDED",
            "HTTP_X_CLUSTER_CLIENT_IP",
            "HTTP_CLIENT_IP",
            "HTTP_FORWARDED_FOR",
            "HTTP_FORWARDED",
            "HTTP_VIA",
            "REMOTE_ADDR"
    };

    /**
     * 获取用户真实IP地址
     *
     * @param request
     * @return
     */
    public static String getClientIpAddress(HttpServletRequest request) {
        String rip = request.getRemoteAddr();
        for (String header : HEADERS_TO_TRY) {
            String ip = request.getHeader(header);
            if (ip != null && ip.length() != 0 && !"unknown".equalsIgnoreCase(ip)) {
                rip = ip;
                break;
            }
        }
        int pos = rip.indexOf(',');
        if (pos >= 0) {
            rip = rip.substring(0, pos);
        }
        return rip;
    }
}
