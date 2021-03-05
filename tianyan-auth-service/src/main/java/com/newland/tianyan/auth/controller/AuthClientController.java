package com.newland.tianyan.auth.controller;

import com.newland.tianyan.common.api.IAuthClientApi;
import com.newland.tianyan.common.model.auth.AuthClientReqDTO;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.provider.client.BaseClientDetails;
import org.springframework.security.oauth2.provider.client.JdbcClientDetailsService;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.util.Collections;

@RestController
public class AuthClientController implements IAuthClientApi {

    private final JdbcClientDetailsService service;
    private final PasswordEncoder encoder;

    public AuthClientController(JdbcClientDetailsService service, PasswordEncoder encoder) {
        this.service = service;
        this.encoder = encoder;
    }

    @Override
    @RequestMapping(value = "/addClient", method = RequestMethod.POST)
    public void addClient(@RequestBody @Valid AuthClientReqDTO request) {
        BaseClientDetails details = new BaseClientDetails();

        details.setClientId(request.getClientId());
        details.setClientSecret(encoder.encode(request.getClientSecret()));
        details.setAuthorizedGrantTypes(Collections.singletonList("client_credentials"));
        details.setAccessTokenValiditySeconds(43200);
        details.addAdditionalInformation("account", request.getAccount());
        details.addAdditionalInformation("app_id", request.getAppId());

        service.addClientDetails(details);
    }

    @Override
    @RequestMapping(value = "/deleteClient", method = RequestMethod.POST)
    public void deleteClient(@RequestBody @Valid  AuthClientReqDTO request) {
        service.removeClientDetails(request.getClientId());
    }

}
