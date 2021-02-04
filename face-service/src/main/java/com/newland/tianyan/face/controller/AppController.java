package com.newland.tianyan.face.controller;

import com.newland.tianyan.common.feign.ImageStoreFeignService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author: RojiaHuang
 * @description:
 * @date: 2021/2/2
 */
@RestController
@RequestMapping("/backend/app")
@RefreshScope
public class AppController {

    @Autowired
    private ImageStoreFeignService imageStoreFeignService;

    @Value("${user.id}")
    private String userId;

    @GetMapping("/helloNacos")
    public String helloNacos() {
        return "你好，nacos！" + userId;
    }

    @GetMapping("/upload")
    public String uploadImage() {
        return imageStoreFeignService.uploadImage();
    }
}
