package com.newland.tianyan.common.model.auth;

import lombok.*;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class AuthClientReqDTO {

    private String account;
    private Long appId;
    private String clientId;
    private String clientSecret;
}
