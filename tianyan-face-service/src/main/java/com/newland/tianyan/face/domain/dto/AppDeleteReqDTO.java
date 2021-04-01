package com.newland.tianyan.face.domain.dto;

import lombok.Data;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

import static com.newland.tianyan.face.constant.BusinessArgumentConstants.MIN_APP_ID;

@Data
public class AppDeleteReqDTO {

    @NotEmpty
    private String account;

    @NotNull
    @Min(1599613749000L)
    private Long appId;

}
