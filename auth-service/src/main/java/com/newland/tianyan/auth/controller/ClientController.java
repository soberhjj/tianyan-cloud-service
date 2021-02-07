package com.newland.tianyan.auth.controller;

import com.newland.tianyan.auth.request.ClientRequest;
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
public class ClientController {

    private final JdbcClientDetailsService service;
    private final PasswordEncoder encoder;

    public ClientController(JdbcClientDetailsService service, PasswordEncoder encoder) {
        this.service = service;
        this.encoder = encoder;
    }

    @RequestMapping(value = "/addClient", method = RequestMethod.POST)
    public void addClient(@RequestBody @Validated ClientRequest request) {
        BaseClientDetails details = new BaseClientDetails();

        details.setClientId(request.getClientId());
        details.setClientSecret(encoder.encode(request.getClientSecret()));
        details.setAuthorizedGrantTypes(Collections.singletonList("client_credentials"));
        details.setAccessTokenValiditySeconds(43200);
        details.addAdditionalInformation("account", request.getAccount());
        details.addAdditionalInformation("app_id", request.getAppId());

        service.addClientDetails(details);
    }

    @RequestMapping(value = "/deleteClient", method = RequestMethod.POST)
    public void deleteClient(@RequestBody @Validated ClientRequest request) {
        service.removeClientDetails(request.getClientId());
    }

}
