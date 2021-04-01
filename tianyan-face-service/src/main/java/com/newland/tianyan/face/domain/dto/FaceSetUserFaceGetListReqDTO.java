package com.newland.tianyan.face.domain.dto;

import com.newland.tianyan.face.constant.VerifyConstant;
import lombok.Builder;
import lombok.Data;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;

import static com.newland.tianyan.face.constant.BusinessArgumentConstants.*;

@Data
public class FaceSetUserFaceGetListReqDTO {

    @NotBlank
    private String account;

    @NotNull
    @Min(1599613749000L)
    private Long appId;

    @NotBlank
    @Pattern(regexp = VerifyConstant.GROUP_ID_OLD)
    private String groupId;

    @NotBlank
    @Pattern(regexp = VerifyConstant.USER_ID_OLD)
    private String userId;

    @Min(0)
    private int startIndex = DEFAULT_PAGE_INDEX;

    @Min(0)
    private int length = DEFAULT_PAGE_SIZE;
}
