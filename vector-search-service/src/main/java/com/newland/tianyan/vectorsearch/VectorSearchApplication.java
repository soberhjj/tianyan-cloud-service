package com.newland.tianyan.vectorsearch;

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
public class VectorSearchApplication {
    public static void main(String[] args) {
        SpringApplication.run(VectorSearchApplication.class, args);
        System.out.println("向量搜索服务启动成功！");
    }
}
