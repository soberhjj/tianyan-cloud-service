package com.newland.tianyan.vectorsearch;

import lombok.extern.slf4j.Slf4j;
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
@Slf4j
public class VectorSearchApplication {
    public static void main(String[] args) {
        SpringApplication.run(VectorSearchApplication.class, args);
        log.info("向量搜索服务启动成功！");
    }
}
