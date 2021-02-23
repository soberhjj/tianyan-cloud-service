package com.newland.tianyan.common.model.auth;

import lombok.*;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class LoginRegisterReqDTO {

    @Pattern(regexp = "^[\\dA-Za-z_\\u4e00-\\u9fa5]{6,32}$")
    private String account;

    @NotBlank
    private String mailbox;

    @Pattern(regexp = "^[\\dA-Za-z_\\u4e00-\\u9fa5]{6,32}$")
    private String password;
}
