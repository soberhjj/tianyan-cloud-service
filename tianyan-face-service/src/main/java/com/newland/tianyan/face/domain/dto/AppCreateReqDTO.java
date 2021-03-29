package com.newland.tianyan.face.domain.dto;

import com.newland.tianya.commons.base.constants.VerifyConstant;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.validator.constraints.Range;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;

@Data
public class AppCreateReqDTO {

    @NotEmpty
    private String account;

    @NotEmpty
    @Pattern(regexp = "^[\\dA-Za-z_\\u4e00-\\u9fa5]{0,32}$")
    private String appName;

    @Pattern(regexp = VerifyConstant.APP_LIST)
    private String apiList;

    @NotEmpty
    private String appInfo;

    @NotNull
    @Range(min = 1, max = 3)
    private Integer type;
}
