package com.newland.tianya.commons.base.model.auth;

import lombok.*;

import javax.validation.constraints.NotBlank;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class LoginResetPasswordReqDTO {

    @NotBlank
    private String mailbox;

    @NotBlank
    private String password;
}
