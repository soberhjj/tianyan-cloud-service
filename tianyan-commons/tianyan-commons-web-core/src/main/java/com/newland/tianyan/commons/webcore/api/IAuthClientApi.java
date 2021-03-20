package com.newland.tianyan.commons.webcore.api;

import com.newland.tianya.commons.base.model.auth.AuthClientReqDTO;
import com.newland.tianyan.commons.webcore.annotation.ApiVersion;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.validation.Valid;

/**
 * @author: RojiaHuang
 * @description:
 * @date: 2021/2/20
 */
@RequestMapping("/auth/v1")
public interface IAuthClientApi {
    @PostMapping("/addClient")
    @ApiVersion(1)
    void addClient(@RequestBody @Valid AuthClientReqDTO request);

    @PostMapping("/deleteClient")
    @ApiVersion(1)
    void deleteClient(@RequestBody @Valid AuthClientReqDTO request);
}
