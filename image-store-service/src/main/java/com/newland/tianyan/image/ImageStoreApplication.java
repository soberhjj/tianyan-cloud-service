package com.newland.tianyan.image;

import com.github.tobato.fastdfs.FdfsClientConfig;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.context.annotation.Import;

/**
 * @author: RojiaHuang
 * @description:
 * @date: 2021/2/2
 */
@EnableDiscoveryClient
@SpringBootApplication
@Import(FdfsClientConfig.class)
public class ImageStoreApplication {
    public static void main(String[] args) {
        SpringApplication.run(ImageStoreApplication.class, args);
        System.out.println("图片服务启动成功！");
    }
}
