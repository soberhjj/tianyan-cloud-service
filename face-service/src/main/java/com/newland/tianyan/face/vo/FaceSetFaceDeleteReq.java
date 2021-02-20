package com.newland.tianyan.face.vo;

import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;

@Getter
@Setter
public class FaceSetFaceDeleteReq {

    @NotBlank
    private String faceId;

    @NotBlank
    @Pattern(regexp = "^[\\dA-Za-z_\\u4e00-\\u9fa5]{0,32}$")
    private String userId;

    @NotBlank
    private String groupId;

    @NotNull
    private Long appId;

    @NotBlank
    private String account;
}