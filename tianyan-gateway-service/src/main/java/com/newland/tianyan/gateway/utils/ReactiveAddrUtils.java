package com.newland.tianyan.gateway.utils;

import com.newland.tianyan.gateway.constant.GatewayErrorEnums;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.http.server.reactive.ServerHttpRequest;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Map;

/**
 * @author: RojiaHuang
 * @description: 获取ip工具
 * @date: 2021/2/27
 */
@Slf4j
public class ReactiveAddrUtils {
    private final static String UNKNOWN_STR = "unknown";
    /**
     * 获取客户端IP地址
     */
    public static String getRemoteAddr(ServerHttpRequest request) {
        Map<String, String> headers = request.getHeaders().toSingleValueMap();
        String ip = headers.get("X-Forwarded-For");
        if (isEmptyIP(ip)) {
            ip = headers.get("Proxy-Client-IP");
            if (isEmptyIP(ip)) {
                ip = headers.get("WL-Proxy-Client-IP");
                if (isEmptyIP(ip)) {
                    ip = headers.get("HTTP_CLIENT_IP");
                    if (isEmptyIP(ip)) {
                        ip = headers.get("HTTP_X_FORWARDED_FOR");
                        if (isEmptyIP(ip)) {
                            ip = request.getRemoteAddress().getAddress().getHostAddress();
                            if ("127.0.0.1".equals(ip) || "0:0:0:0:0:0:0:1".equals(ip)) {
                                // 根据网卡取本机配置的IP
                                ip = getLocalAddr();
                            }
                        }
                    }
                }
            }
        } else if (ip.length() > 15) {
            String[] ips = ip.split(",");
            for (int index = 0; index < ips.length; index++) {
                String strIp = ips[index];
                if (!isEmptyIP(ip)) {
                    ip = strIp;
                    break;
                }
            }
        }
//        if (ip.equals("192.168.2.219")){
//            throw GatewayErrorEnums.DEMO.toException();
//        }
        return ip;
    }

    private static boolean isEmptyIP(String ip) {
        if (StringUtils.isEmpty(ip) || UNKNOWN_STR.equalsIgnoreCase(ip)) {
            return true;
        }
        return false;
    }

    /**
     * 获取本机的IP地址
     */
    public static String getLocalAddr() {
        try {
            return InetAddress.getLocalHost().getHostAddress();
        } catch (UnknownHostException e) {
            log.error("InetAddress.getLocalHost()-error", e);
        }
        return "";
    }
}