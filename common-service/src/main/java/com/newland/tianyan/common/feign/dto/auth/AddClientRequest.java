package com.newland.tianyan.common.feign.dto.auth;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class AddClientRequest {

    private String account;
    private Long appId;
    private String clientId;
    private String clientSecret;
}