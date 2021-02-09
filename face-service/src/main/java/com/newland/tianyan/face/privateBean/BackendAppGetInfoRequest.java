package com.newland.tianyan.face.privateBean;

import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Getter
@Setter
public class BackendAppGetInfoRequest {

    @NotBlank
    private String account;

    @NotNull
    @Min(1599613749000L)
    private Long appId;

}
