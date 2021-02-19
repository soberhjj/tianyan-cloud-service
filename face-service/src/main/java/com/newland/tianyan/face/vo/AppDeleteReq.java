package com.newland.tianyan.face.vo;

import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

@Getter
@Setter
public class AppDeleteReq {

    @NotEmpty
    private String account;

    @NotNull
    @Min(1)
    private Long appId;

}
