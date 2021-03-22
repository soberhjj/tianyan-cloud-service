package com.newland.tianyan.face.domain.dto;

import lombok.Data;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

@Data
public class AppDeleteReqDTO {

    @NotEmpty
    private String account;

    @NotNull
    @Min(1)
    private Long appId;

}
