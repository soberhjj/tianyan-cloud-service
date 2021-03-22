package com.newland.tianyan.auth.controller;

import com.newland.tianya.commons.base.constants.TaskTypeEnums;
import com.newland.tianya.commons.base.model.auth.LoginCheckUniqueReqDTO;
import com.newland.tianya.commons.base.model.auth.LoginGetInfoReqDTO;
import com.newland.tianya.commons.base.model.auth.LoginRegisterReqDTO;
import com.newland.tianya.commons.base.model.proto.NLBackend;
import com.newland.tianya.commons.base.utils.ProtobufUtils;
import com.newland.tianyan.auth.entity.Account;
import com.newland.tianyan.auth.service.ILoginService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

@RestController
@RequestMapping("/login")
public class AuthLoginController {

    private final ILoginService loginService;

    @Autowired
    public AuthLoginController(ILoginService loginService) {
        this.loginService = loginService;
    }

    @RequestMapping(value = "/checkUnique", method = RequestMethod.POST)
    public Object checkUnique(@RequestBody @Valid LoginCheckUniqueReqDTO receive) {
        NLBackend.BackendAllRequest request = ProtobufUtils.toBackendAllRequest(receive, TaskTypeEnums.BACKEND_APP_GET_INFO);
        loginService.checkUnique(request);
        return ProtobufUtils.buildLoginSendMessage();
    }

    @RequestMapping(value = "/getInfo", method = RequestMethod.POST)
    public Object getInfo(@RequestBody @Valid LoginGetInfoReqDTO receive) {
        NLBackend.BackendAllRequest request = ProtobufUtils.toBackendAllRequest(receive, TaskTypeEnums.BACKEND_APP_GET_INFO);
        Account info = loginService.getInfo(request);
        return ProtobufUtils.buildLoginSendMessage(info.getAccount(), info.getMailbox());

    }

    @RequestMapping(value = "/register", method = RequestMethod.POST)
    public NLBackend.BackendLoginSendMessage update(@RequestBody @Valid LoginRegisterReqDTO receive) {
        NLBackend.BackendAllRequest request = ProtobufUtils.toBackendAllRequest(receive, TaskTypeEnums.BACKEND_APP_GET_INFO);
        loginService.register(request);
        return ProtobufUtils.buildLoginSendMessage();
    }

    @RequestMapping(value = "/resetPassword", method = RequestMethod.POST)
    public NLBackend.BackendLoginSendMessage resetPassword(@RequestBody @Valid LoginRegisterReqDTO receive) {
        NLBackend.BackendAllRequest request = ProtobufUtils.toBackendAllRequest(receive, TaskTypeEnums.BACKEND_APP_GET_INFO);
        loginService.restPassword(request);
        return ProtobufUtils.buildLoginSendMessage();
    }

}
