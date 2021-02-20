package com.newland.tianyan.common.model.authService;

import com.newland.tianyan.common.model.authService.dto.AuthClientReqDO;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

/**
 * @author: RojiaHuang
 * @description:
 * @date: 2021/2/20
 */
public interface IAuthServiceApi {
    @PostMapping("/addClient")
    void addClient(@RequestBody AuthClientReqDO request);

    @PostMapping("/deleteClient")
    void deleteClient(@RequestBody AuthClientReqDO request);
}
