package com.newland.tianyan.auth.request;

import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Getter
@Setter
public class ClientRequest {

    @NotBlank
    private String account;

    @NotNull
    private Long appId;

    @NotBlank
    private String clientId;

    @NotBlank
    private String clientSecret;

}
