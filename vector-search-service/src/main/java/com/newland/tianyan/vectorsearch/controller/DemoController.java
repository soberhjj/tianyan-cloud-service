package com.newland.tianyan.vectorsearch.controller;

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
//@RefreshScope
public class DemoController {

    @Autowired
    private DemoService demoService;

    @GetMapping("/helloNacos")
    public String helloNacos() {
        return "你好，nacos！" + demoService.test();
    }

}
