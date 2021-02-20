package com.newland.tianyan.common.model.authService;

import com.newland.tianyan.common.model.authService.dto.AuthClientReqDTO;
import org.springframework.web.bind.annotation.PostMapping;

/**
 * @author: RojiaHuang
 * @description:
 * @date: 2021/2/20
 */
public interface IClientApi {
    @PostMapping("/addClient")
    void addClient(AuthClientReqDTO request);

    @PostMapping("/deleteClient")
    void deleteClient(AuthClientReqDTO request);
}
