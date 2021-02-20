package com.newland.tianyan.common.model.authService;

import com.newland.tianyan.common.model.authService.dto.LoginCheckUniqueReqDTO;
import com.newland.tianyan.common.model.authService.dto.LoginGetInfoReqDTO;
import com.newland.tianyan.common.model.authService.dto.LoginRegisterReqDTO;
import com.newland.tianyan.common.utils.message.NLBackend;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

/**
 * @author: RojiaHuang
 * @description:
 * @date: 2021/2/20
 */
public interface ILoginApi {
    @PostMapping(value = "/checkUnique")
    Object checkUnique(LoginCheckUniqueReqDTO receive);

    @RequestMapping(value = "/getInfo", method = RequestMethod.POST)
    Object getInfo(@RequestBody LoginGetInfoReqDTO receive);

    @RequestMapping(value = "/register", method = RequestMethod.POST)
    NLBackend.BackendLoginSendMessage update(@RequestBody @Validated LoginRegisterReqDTO receive);

    @RequestMapping(value = "/resetPassword", method = RequestMethod.POST)
    NLBackend.BackendLoginSendMessage resetPassword(@RequestBody @Validated LoginRegisterReqDTO receive);
}
