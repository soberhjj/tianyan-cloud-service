package com.newland.tianyan.face.domain.dto;

import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;

@Getter
@Setter
public class FaceSetGroupDeleteReqDTO {

    @NotBlank
    private String account;

    @Min(0)
    private Long appId;

    @NotBlank
    private String groupId;
}
