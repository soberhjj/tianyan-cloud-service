package com.newland.tianyan.image;

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
public class ImageStoreApplication {
    public static void main(String[] args) {
        SpringApplication.run(ImageStoreApplication.class, args);
        System.out.println("图片服务启动成功！");
    }
}
