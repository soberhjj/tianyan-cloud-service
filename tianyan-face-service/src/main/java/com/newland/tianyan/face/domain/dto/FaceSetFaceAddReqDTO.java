package com.newland.tianyan.face.domain.dto;

import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;

@Getter
@Setter
public class FaceSetFaceAddReqDTO {

    @NotBlank
    private String image;

    @NotBlank
    @Pattern(regexp = "^[\\dA-Za-z_\\u4e00-\\u9fa5]{0,32}$")
    private String userId;

    @Pattern(regexp = "^[\\dA-Za-z_\\u4e00-\\u9fa5]{0,32}$")
    private String userName;

    @NotBlank
    @Pattern(regexp = "^[\\dA-Za-z_\\u4e00-\\u9fa5]{0,32}$")
    private String groupId;

    @NotNull
    private long appId;

    @NotBlank
    private String account;

    private String userInfo;
    /**
     * action_type取值有两种("append"和"replace")。
     * */
    private String actionType;

    private int type;

    private int qualityControl;
}
