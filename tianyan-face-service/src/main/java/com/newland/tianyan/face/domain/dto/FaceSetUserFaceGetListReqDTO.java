package com.newland.tianyan.face.domain.dto;

import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;

@Getter
@Setter
public class FaceSetUserFaceGetListReqDTO {

    @NotBlank
    private String account;

    @NotNull
    private Long appId;

    @NotBlank
    @Pattern(regexp = "^[\\dA-Za-z_\\u4e00-\\u9fa5]{0,32}$")
    private String groupId;

    @NotBlank
    @Pattern(regexp = "^[\\dA-Za-z_\\u4e00-\\u9fa5]{0,32}$")
    private String userId;

    private int startIndex;
    private int length;
}
