package com.newland.tianyan.common.model.authService.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class AuthClientReqDTO {

    private String account;
    private Long appId;
    private String clientId;
    private String clientSecret;
}
