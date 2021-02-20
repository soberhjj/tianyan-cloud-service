package com.newland.tianyan.auth.controller;

import com.newland.tianyan.auth.entity.Account;
import com.newland.tianyan.auth.service.LoginService;
import com.newland.tianyan.common.model.authService.ILoginApi;
import com.newland.tianyan.common.model.authService.dto.LoginCheckUniqueReqDTO;
import com.newland.tianyan.common.model.authService.dto.LoginGetInfoReqDTO;
import com.newland.tianyan.common.model.authService.dto.LoginRegisterReqDTO;
import com.newland.tianyan.common.utils.constans.TaskType;
import com.newland.tianyan.common.utils.message.NLBackend;
import com.newland.tianyan.common.utils.utils.JsonErrorObject;
import com.newland.tianyan.common.model.proto.LogUtils;
import com.newland.tianyan.common.model.proto.ProtobufUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/login")
public class LoginController implements ILoginApi {

    private final LoginService loginService;

    @Autowired
    public LoginController(LoginService loginService) {
        this.loginService = loginService;
    }

    @Override
    @RequestMapping(value = "/checkUnique", method = RequestMethod.POST)
    public Object checkUnique(@RequestBody @Validated LoginCheckUniqueReqDTO receive) {
        boolean unique = StringUtils.isEmpty(receive.getAccount()) ^ StringUtils.isEmpty(receive.getMailbox());
        if (!unique) {
            return new JsonErrorObject(LogUtils.getLogId(), 6100, "invalid param");
        }
        NLBackend.BackendAllRequest request = ProtobufUtils.toBackendAllRequest(receive, TaskType.BACKEND_APP_GET_INFO);
        loginService.checkUnique(request);
        return ProtobufUtils.buildLoginSendMessage();
    }

    @Override
    @RequestMapping(value = "/getInfo", method = RequestMethod.POST)
    public Object getInfo(@RequestBody LoginGetInfoReqDTO receive) {
        boolean unique = StringUtils.isEmpty(receive.getAccount()) ^ StringUtils.isEmpty(receive.getMailbox());
        if (!unique) {
            return new JsonErrorObject(LogUtils.getLogId(), 6100, "invalid param");
        }
        NLBackend.BackendAllRequest request = ProtobufUtils.toBackendAllRequest(receive, TaskType.BACKEND_APP_GET_INFO);
        Account info = loginService.getInfo(request);
        return ProtobufUtils.buildLoginSendMessage(info.getAccount(), info.getMailbox());

    }

    @Override
    @RequestMapping(value = "/register", method = RequestMethod.POST)
    public NLBackend.BackendLoginSendMessage update(@RequestBody @Validated LoginRegisterReqDTO receive) {
        NLBackend.BackendAllRequest request = ProtobufUtils.toBackendAllRequest(receive, TaskType.BACKEND_APP_GET_INFO);
        loginService.register(request);
        return ProtobufUtils.buildLoginSendMessage();
    }

    @Override
    @RequestMapping(value = "/resetPassword", method = RequestMethod.POST)
    public NLBackend.BackendLoginSendMessage resetPassword(@RequestBody @Validated LoginRegisterReqDTO receive) {
        NLBackend.BackendAllRequest request = ProtobufUtils.toBackendAllRequest(receive, TaskType.BACKEND_APP_GET_INFO);
        loginService.restPassword(request);
        return ProtobufUtils.buildLoginSendMessage();
    }

}
