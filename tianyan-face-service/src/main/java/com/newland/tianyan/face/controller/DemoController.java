package com.newland.tianyan.face.controller;


import com.newland.tianyan.common.exception.CommonException;
import com.newland.tianyan.common.exception.global.system.SysException;
import com.newland.tianyan.common.model.auth.AuthClientReqDTO;
import com.newland.tianyan.face.feign.client.AuthClientFeignService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author: RojiaHuang
 * @description:
 * @date: 2021/2/2
 */
@RestController
public class DemoController {

    @Autowired
    private AuthClientFeignService authClientFeignService;

    @PostMapping("/test")
    public String test(@RequestBody AuthClientReqDTO req) {
        String result = authClientFeignService.test(req);
        return result + " fallback!";
    }

    @PostMapping("/test1")
    public String test1(@RequestBody AuthClientReqDTO req) {
        String result = "";
        try {
            result = authClientFeignService.test(req);
        }catch (Exception e){
            if (e instanceof CommonException){
                throw new SysException(((CommonException) e).getErrorCode(),((CommonException) e).getErrorMsg());
            }
        }
        return result;
    }
}
