package com.newland.tianyan.auth.privateBean;

import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotBlank;

@Getter
@Setter
public class BackendLoginResetPasswordRequest {

    @NotBlank
    private String mailbox;

    @NotBlank
    private String password;
}
