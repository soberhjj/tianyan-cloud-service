package com.newland.tianyan.common.model.authservice.dto;


import lombok.Getter;
import lombok.Setter;


@Getter
@Setter
public class LoginCheckUniqueReqDTO {

    private String account;

    private String mailbox;


}