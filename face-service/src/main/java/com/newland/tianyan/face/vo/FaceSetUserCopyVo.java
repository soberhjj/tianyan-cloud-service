package com.newland.tianyan.face.vo;

import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Getter
@Setter
public class FaceSetUserCopyVo {

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
