package com.newland.tianyan.face.remote.dto.auth;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class AddClientRequest {

    private String account;
    private Long appId;
    private String clientId;
    private String clientSecret;
}
