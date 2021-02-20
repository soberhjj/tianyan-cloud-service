package com.newland.tianyan.common.model.authService.dto;

import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotBlank;

@Getter
@Setter
public class LoginResetPasswordReqDO {

    @NotBlank
    private String mailbox;

    @NotBlank
    private String password;
}
