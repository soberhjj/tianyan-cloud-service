package com.newland.tianyan.face.domain.dto;

import com.newland.tianyan.face.constant.VerifyConstant;
import lombok.Data;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;

import static com.newland.tianyan.face.constant.BusinessArgumentConstants.MIN_APP_ID;

@Data
public class AppUpdateReqDTO {

    @NotEmpty
    private String account;

    @NotEmpty
    @Pattern(regexp = VerifyConstant.APP_NAME)
    private String appName;

    @Pattern(regexp = VerifyConstant.APP_LIST)
    private String apiList;

    @NotEmpty
    private String appInfo;

    @Min(1599613749000L)
    @NotNull
    private Long appId;

}
