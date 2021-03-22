package com.newland.tianyan.face.domain.dto;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Pattern;

@Data
public class AppUpdateReqDTO {

    @NotEmpty
    private String account;

    @NotEmpty
    @Pattern(regexp = "^[\\dA-Za-z_\\u4e00-\\u9fa5]{0,32}$")
    private String appName;

    @Pattern(regexp = "^[1-5,]{1,9}$")
    private String apiList;

    @NotEmpty
    private String appInfo;

    @Min(1)
    private Long appId;

}
