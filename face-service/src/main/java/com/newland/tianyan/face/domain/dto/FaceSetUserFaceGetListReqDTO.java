package com.newland.tianyan.face.domain.dto;

import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Getter
@Setter
public class FaceSetUserFaceGetListReqDTO {

    @NotBlank
    private String account;

    @NotNull
    private Long appId;

    @NotBlank
    private String groupId;

    @NotBlank
    private String userId;

    private int startIndex;
    private int length;
}