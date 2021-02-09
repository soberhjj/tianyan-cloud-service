//package com.newland.tianyan.face.controller;
//
//import com.newland.tianyan.face.remote.AuthFeignService;
//import com.newland.tianyan.face.remote.dto.auth.AddClientRequest;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.web.bind.annotation.PostMapping;
//import org.springframework.web.bind.annotation.RestController;
//
//import java.util.Random;
//
///**
// * @author: RojiaHuang
// * @description:
// * @date: 2021/2/2
// */
//@RestController
//public class DemoController {
//
//    @Autowired
//    private AuthFeignService authFeignService;
//
//    @PostMapping("/addClient")
//    public void addClient() {
//        AddClientRequest addClientRequest = new AddClientRequest();
//        addClientRequest.setAccount("test2021");
//        addClientRequest.setAppId(new Random(20).nextLong());
//        addClientRequest.setClientId("testClientFeign");
//        addClientRequest.setClientSecret("testClientFeign");
//        authFeignService.addClient(addClientRequest);
//    }
//
//}
