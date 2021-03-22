package com.newland.tianyan.face.domain.dto;

import lombok.Data;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;

@Data
public class FaceSetGroupGetListReqDTO {

    @NotBlank
    private String account;

    @NotNull
    @Min(1)
    private Long appId;

    @Pattern(regexp = "^[\\dA-Za-z_\\u4e00-\\u9fa5]{0,32}$")
    private String groupId;

    @Min(0)
    private int startIndex;

    @Min(0)
    private int length;
}
