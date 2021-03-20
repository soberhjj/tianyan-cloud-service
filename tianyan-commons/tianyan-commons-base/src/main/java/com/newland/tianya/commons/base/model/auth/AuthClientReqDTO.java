package com.newland.tianya.commons.base.model.auth;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

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
