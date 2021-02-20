package com.newland.tianyan.face.vo;

import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;

@Getter
@Setter
public class FaceSetUserDeleteReq {

    @NotBlank
    private String account;

    @Min(0)
    @NonNull
    private Long appId;

    @NotBlank
    private String groupId;

    @NotBlank
    private String userId;
}