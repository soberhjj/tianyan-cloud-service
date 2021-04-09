package com.newland.tianyan.face.domain.dto;

import com.newland.tianyan.face.constant.VerifyConstant;
import lombok.Data;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;

import static com.newland.tianyan.face.constant.BusinessArgumentConstants.MIN_APP_ID;

@Data
public class FaceSetUserCopyReqDTO {

    @NotBlank
    private String account;

    @NotNull
    @Min(1L)
    private Long appId;

    @NotBlank
    @Pattern(regexp = VerifyConstant.GROUP_ID_OLD)
    private String srcGroupId;

    @NotBlank
    @Pattern(regexp = VerifyConstant.GROUP_ID_OLD)
    private String dstGroupId;

    @NotBlank
    @Pattern(regexp = VerifyConstant.USER_ID_OLD)
    private String userId;

}
