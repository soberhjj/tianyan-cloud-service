package com.newland.tianyan.face.domain.dto;

import com.newland.tianyan.face.constant.VerifyConstant;
import lombok.Data;

import javax.validation.constraints.*;

import static com.newland.tianyan.face.constant.BusinessArgumentConstants.MIN_APP_ID;

@Data
public class FaceSetGroupAddReqDTO {

    @NotEmpty
    @Pattern(regexp = VerifyConstant.GROUP_ID)
    private String groupId;

    @NotNull
    @Min(1599613749000L)
    private Long appId;

    @NotBlank
    private String account;
}
