package com.newland.tianyan.face.domain.dto;

import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;

@Getter
@Setter
public class FaceSetUserGetListReqDTO {

    //length not zero
    @NotBlank
    private String account;

    //length can be zero
    @Min(0)
    @NotNull
    private Long appId;

    @NotBlank
    @Pattern(regexp = "^[\\dA-Za-z_\\u4e00-\\u9fa5]{0,32}$")
    private String groupId;

    // 指定时为搜索,不指定则返回全部
    @Pattern(regexp = "^[\\dA-Za-z_\\u4e00-\\u9fa5]{0,32}$")
    private String userId;

    private Integer startIndex;
    private Integer length;
}
