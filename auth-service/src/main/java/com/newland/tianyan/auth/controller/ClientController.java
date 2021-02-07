package com.newland.tianyan.auth.controller;

import com.netflix.client.ClientRequest;
import org.springframework.web.bind.annotation.RequestBody;
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
    public void addClient(@RequestBody com.netflix.client.ClientRequest request) {

    }

    @RequestMapping(value = "/deleteClient", method = RequestMethod.POST)
    public void deleteClient(@RequestBody ClientRequest request){

    }
}
