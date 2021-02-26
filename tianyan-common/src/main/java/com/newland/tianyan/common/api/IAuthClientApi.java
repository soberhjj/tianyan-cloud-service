package com.newland.tianyan.common.api;

import com.newland.tianyan.common.model.auth.AuthClientReqDTO;
import com.newland.tianyan.common.version.ApiVersion;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * @author: RojiaHuang
 * @description:
 * @date: 2021/2/20
 */
@RequestMapping("/auth/v1")
public interface IAuthClientApi {
    @PostMapping("/addClient")
    @ApiVersion(1)
    void addClient(@RequestBody AuthClientReqDTO request);

    @PostMapping("/deleteClient")
    @ApiVersion(1)
    void deleteClient(@RequestBody AuthClientReqDTO request);
}
