package com.newland.tianyan.face.domain.dto;

import com.newland.tianya.commons.base.constants.VerifyConstant;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;

@Data
public class FaceSetFaceAddReqDTO {

    @NotBlank
    private String image;

    @NotBlank
    @Pattern(regexp = VerifyConstant.USER_ID)
    private String userId;

    @Pattern(regexp = "^[\\dA-Za-z_\\u4e00-\\u9fa5]{0,32}$")
    private String userName;

    @NotBlank
    @Pattern(regexp = VerifyConstant.GROUP_ID)
    private String groupId;

    @NotNull
    private long appId;

    @NotBlank
    private String account;

    private String userInfo;
    /**
     * action_type取值有两种("append"和"replace")。
     */
    private String actionType;

    private int type;

    private int qualityControl;
}
