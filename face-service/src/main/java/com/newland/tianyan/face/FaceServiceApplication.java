package com.newland.tianyan.face;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;

/**
 * @author: RojiaHuang
 * @description:
 * @date: 2021/2/2
 */
@EnableFeignClients(basePackages = "com.newland.tianyan.common.feign")
@EnableDiscoveryClient
@SpringBootApplication(scanBasePackages = {"com.newland.tianyan.face.*"})
public class FaceServiceApplication {
    public static void main(String[] args) {
        SpringApplication.run(FaceServiceApplication.class, args);
        System.out.println("人脸服务启动成功！");
    }
}
