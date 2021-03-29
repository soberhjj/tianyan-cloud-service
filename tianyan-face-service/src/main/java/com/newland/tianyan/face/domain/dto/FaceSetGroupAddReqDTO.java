package com.newland.tianyan.face.domain.dto;

import com.newland.tianya.commons.base.constants.VerifyConstant;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;

@Data
public class FaceSetGroupAddReqDTO {

    @NotEmpty
    @Pattern(regexp = VerifyConstant.GROUP_ID)
    private String groupId;

    @NotNull
    private Long appId;

    @NotBlank
    private String account;
}
