package com.newland.tianyan.face.domain.dto;

import com.newland.tianyan.face.constant.VerifyConstant;
import lombok.Data;
import org.hibernate.validator.constraints.Range;

import javax.validation.constraints.Max;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;

@Data
public class AppCreateReqDTO {

    @NotEmpty
    private String account;

    @NotEmpty
    @Pattern(regexp = VerifyConstant.APP_NAME)
    private String appName;

    @Pattern(regexp = VerifyConstant.APP_LIST)
    private String apiList;

    @NotEmpty
    private String appInfo;

    @NotNull
    @Range(min = 1, max = 3)
    private Integer type;
}
