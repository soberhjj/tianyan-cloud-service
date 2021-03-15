package com.newland.tianyan.auth.controller;


import com.newland.tianyan.common.model.auth.AuthClientReqDTO;
import com.newland.tianyan.common.utils.JsonUtils;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

/**
 * @author: RojiaHuang
 * @description:
 * @date: 2021/2/2
 */
@RestController
@RequestMapping("/auth/v1")
public class DemoController {

    @PostMapping("/test")
    public String test(@RequestBody @Valid AuthClientReqDTO request) {
//        if ("huangtest".equals(request.getAccount())) {
//            throw BusinessErrorEnums.DEMO.toException();
//        }
        return "Hey!--->" + JsonUtils.toJson(request);
    }

}
