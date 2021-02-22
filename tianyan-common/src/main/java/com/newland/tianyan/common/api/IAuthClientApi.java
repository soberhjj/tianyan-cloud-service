package com.newland.tianyan.common.api;

import com.newland.tianyan.common.model.authservice.AuthClientReqDTO;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

/**
 * @author: RojiaHuang
 * @description:
 * @date: 2021/2/20
 */
public interface IAuthClientApi {
    @PostMapping("/addClient")
    void addClient(@RequestBody AuthClientReqDTO request);

    @PostMapping("/deleteClient")
    void deleteClient(@RequestBody AuthClientReqDTO request);
}
