package com.newland.tianyan.auth.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
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
