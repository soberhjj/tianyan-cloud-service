package com.newland.tianyan.gateway.support;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.http.server.reactive.ServerHttpRequest;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.*;

/**
 * 网关获取客户端ip工具类
 *
 * @author: RojiaHuang
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
        if (isEmptyIp(ip)) {
            List<String> searchIpHeads = Arrays.asList("Proxy-Client-IP", "WL-Proxy-Client-IP", "HTTP_CLIENT_IP", "HTTP_X_FORWARDED_FOR");
            for (String head : searchIpHeads) {
                ip = headers.get(head);
                if (!isEmptyIp(ip)) {
                    break;
                } else if ("HTTP_X_FORWARDED_FOR".equals(head)) {
                    ip = Objects.requireNonNull(request.getRemoteAddress()).getAddress().getHostAddress();
                    if ("127.0.0.1".equals(ip) || "0:0:0:0:0:0:0:1".equals(ip)) {
                        // 根据网卡取本机配置的IP
                        ip = getLocalAddr();
                    }
                }
            }

        } else if (ip.length() > 15) {
            String[] ips = ip.split(",");
            for (String strIp : ips) {
                if (!isEmptyIp(ip)) {
                    ip = strIp;
                    break;
                }
            }
        }
        return ip;
    }

    private static boolean isEmptyIp(String ip) {
        return StringUtils.isEmpty(ip) || UNKNOWN_STR.equalsIgnoreCase(ip);
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
