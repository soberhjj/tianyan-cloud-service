package com.newland.tianyan.gateway;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

/**
 * @author: RojiaHuang
 * @description:
 * @date: 2021/2/3
 */
@EnableDiscoveryClient
@SpringBootApplication()
@Slf4j
public class GatewayServiceApplication {
    public static void main(String[] args) {
        SpringApplication.run(GatewayServiceApplication.class, args);
        log.info("网关服务启动成功！");
    }
}
