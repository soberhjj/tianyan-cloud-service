package com.newland.tianya.commons.base.model.auth;


import lombok.*;

import javax.validation.constraints.NotBlank;


@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class LoginGetInfoReqDTO {
    @NotBlank
    @NonNull
    private String account;

    @NotBlank
    @NonNull
    private String mailbox;


}
