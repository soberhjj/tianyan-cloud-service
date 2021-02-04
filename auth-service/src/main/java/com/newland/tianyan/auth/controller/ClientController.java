package com.newland.tianyan.auth.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author: RojiaHuang
 * @description:
 * @date: 2021/2/2
 */
@RestController
public class ClientController {

    @RequestMapping(value = "/addClient", method = RequestMethod.POST)
    public String addClient() {
        return "你好，nacos！";
    }
}
