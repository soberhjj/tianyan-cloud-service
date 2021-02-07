package com.newland.tianyan.auth.privateBean;


import lombok.Getter;
import lombok.Setter;


@Getter
@Setter
public class BackendLoginCheckUniqueRequest {

    private String account;

    private String mailbox;


}
