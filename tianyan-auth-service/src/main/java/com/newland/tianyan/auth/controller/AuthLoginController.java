package com.newland.tianyan.auth.controller;

import com.newland.tianyan.auth.entity.Account;
import com.newland.tianyan.auth.service.LoginServiceImpl;
import com.newland.tianyan.common.constans.TaskType;
import com.newland.tianyan.common.model.auth.LoginCheckUniqueReqDTO;
import com.newland.tianyan.common.model.auth.LoginGetInfoReqDTO;
import com.newland.tianyan.common.model.auth.LoginRegisterReqDTO;
import com.newland.tianyan.common.utils.JsonErrorObject;
import com.newland.tianyan.common.utils.LogUtils;
import com.newland.tianyan.common.utils.ProtobufUtils;
import com.newland.tianyan.common.utils.message.NLBackend;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

@RestController
@RequestMapping("/login")
public class AuthLoginController {

    private final LoginServiceImpl loginServiceImpl;

    @Autowired
    public AuthLoginController(LoginServiceImpl loginServiceImpl) {
        this.loginServiceImpl = loginServiceImpl;
    }

    @RequestMapping(value = "/checkUnique", method = RequestMethod.POST)
    public Object checkUnique(@RequestBody @Valid LoginCheckUniqueReqDTO receive) {
        NLBackend.BackendAllRequest request = ProtobufUtils.toBackendAllRequest(receive, TaskType.BACKEND_APP_GET_INFO);
        loginServiceImpl.checkUnique(request);
        return ProtobufUtils.buildLoginSendMessage();
    }

    @RequestMapping(value = "/getInfo", method = RequestMethod.POST)
    public Object getInfo(@RequestBody @Valid LoginGetInfoReqDTO receive) {
        NLBackend.BackendAllRequest request = ProtobufUtils.toBackendAllRequest(receive, TaskType.BACKEND_APP_GET_INFO);
        Account info = loginServiceImpl.getInfo(request);
        return ProtobufUtils.buildLoginSendMessage(info.getAccount(), info.getMailbox());

    }

    @RequestMapping(value = "/register", method = RequestMethod.POST)
    public NLBackend.BackendLoginSendMessage update(@RequestBody @Valid LoginRegisterReqDTO receive) {
        NLBackend.BackendAllRequest request = ProtobufUtils.toBackendAllRequest(receive, TaskType.BACKEND_APP_GET_INFO);
        loginServiceImpl.register(request);
        return ProtobufUtils.buildLoginSendMessage();
    }

    @RequestMapping(value = "/resetPassword", method = RequestMethod.POST)
    public NLBackend.BackendLoginSendMessage resetPassword(@RequestBody @Valid LoginRegisterReqDTO receive) {
        NLBackend.BackendAllRequest request = ProtobufUtils.toBackendAllRequest(receive, TaskType.BACKEND_APP_GET_INFO);
        loginServiceImpl.restPassword(request);
        return ProtobufUtils.buildLoginSendMessage();
    }

}
