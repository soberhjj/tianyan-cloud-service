package com.newland.tianyan.common.utils;

import org.springframework.boot.web.context.WebServerInitializedEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;

import java.net.InetAddress;
import java.net.UnknownHostException;

/**
 * @author: RojiaHuang
 * @description:
 * @date: 2021/2/25
 */
@Component
public class ServerAddressUtils implements ApplicationListener<WebServerInitializedEvent> {

    private int serverPort;

    @Override
    public void onApplicationEvent(WebServerInitializedEvent event) {
        this.serverPort = event.getWebServer().getPort();
    }

    public String getServerAddress() throws Exception {
        return getIp() + ":" + this.serverPort;
    }

    private String getIp() throws UnknownHostException {
        return InetAddress.getLocalHost().getHostAddress();
    }

}

