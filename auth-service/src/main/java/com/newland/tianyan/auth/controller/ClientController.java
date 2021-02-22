package com.newland.tianyan.auth.controller;

import com.newland.tianyan.common.model.authservice.IClientApi;
import com.newland.tianyan.common.model.authservice.dto.AuthClientReqDTO;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.provider.client.BaseClientDetails;
import org.springframework.security.oauth2.provider.client.JdbcClientDetailsService;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.Collections;

@RestController
public class ClientController implements IClientApi {

    private final JdbcClientDetailsService service;
    private final PasswordEncoder encoder;

    public ClientController(JdbcClientDetailsService service, PasswordEncoder encoder) {
        this.service = service;
        this.encoder = encoder;
    }

    @Override
    @RequestMapping(value = "/addClient", method = RequestMethod.POST)
    public void addClient(@RequestBody @Validated AuthClientReqDTO request) {
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
    public void deleteClient(@RequestBody @Validated AuthClientReqDTO request) {
        service.removeClientDetails(request.getClientId());
    }

}
