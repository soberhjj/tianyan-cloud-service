package com.newland.tianyan.face;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableOAuth2Client;

/**
 * @author: RojiaHuang
 * @description:
 * @date: 2021/2/2
 */
@EnableAsync
@EnableDiscoveryClient
@SpringBootApplication
@EnableAspectJAutoProxy
@EnableFeignClients
@EnableOAuth2Client
@Slf4j
public class FaceServiceApplication {
    public static void main(String[] args) {
        SpringApplication.run(FaceServiceApplication.class, args);
        log.info("人脸服务启动成功！");
    }
}
