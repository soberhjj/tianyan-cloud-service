package com.newland.tianyan.auth.controller;


import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author: RojiaHuang
 * @description:
 * @date: 2021/2/2
 */
@RestController
public class DemoController {

    @PostMapping("/test")
    public String addClient() {
        return "Hey!";
    }

}
