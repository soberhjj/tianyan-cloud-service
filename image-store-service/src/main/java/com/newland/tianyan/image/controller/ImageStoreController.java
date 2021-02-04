package com.newland.tianyan.image.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author: RojiaHuang
 * @description:
 * @date: 2021/2/2
 */
@RestController
public class ImageStoreController {

    @GetMapping("/uploadImage")
    public String uploadImage() {
        return "你好，nacos！提交文件至服务器";
    }
}
