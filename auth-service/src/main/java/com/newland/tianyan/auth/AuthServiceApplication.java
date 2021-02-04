package com.newland.tianyan.auth;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

/**
 * @author: RojiaHuang
 * @description:
 * @date: 2021/2/2
 */
@EnableDiscoveryClient
@SpringBootApplication
public class AuthServiceApplication {
    public static void main(String[] args) {
        SpringApplication.run(AuthServiceApplication.class, args);
        System.out.println("授权服务启动成功！");
    }
}
