package com.newland.tianyan.common.model.auth;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;


@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class LoginCheckUniqueReqDTO {

    private String account;

    private String mailbox;


}
