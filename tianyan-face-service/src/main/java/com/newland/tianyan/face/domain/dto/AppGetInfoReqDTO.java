package com.newland.tianyan.face.domain.dto;

import lombok.Data;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Data
public class AppGetInfoReqDTO {

    @NotBlank
    private String account;

    @NotNull
    @Min(1599613749000L)
    private Long appId;

}
