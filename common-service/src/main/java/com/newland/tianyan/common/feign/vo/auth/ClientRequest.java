package com.newland.tianyan.common.feign.vo.auth;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ClientRequest {

    private String account;
    private Long appId;
    private String clientId;
    private String clientSecret;
}
