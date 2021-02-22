package com.newland.tianyan.face.domain.dto;

import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Getter
@Setter
public class FaceSetUserCopyReqDTO {

    @NotBlank
    private String account;

    @NotBlank
    private String userId;

    @NotNull
    private Long appId;

    @NotBlank
    private String dstGroupId;

    @NotBlank
    private String srcGroupId;

}
