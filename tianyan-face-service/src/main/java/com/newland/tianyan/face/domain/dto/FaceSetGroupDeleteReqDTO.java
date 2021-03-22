package com.newland.tianyan.face.domain.dto;

import lombok.Data;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;

@Data
public class FaceSetGroupDeleteReqDTO {

    @NotBlank
    private String account;

    @Min(0)
    private Long appId;

    @NotBlank
    @Pattern(regexp = "^[\\dA-Za-z_\\u4e00-\\u9fa5]{0,32}$")
    private String groupId;
}
