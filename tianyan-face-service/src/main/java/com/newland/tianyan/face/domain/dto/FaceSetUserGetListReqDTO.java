package com.newland.tianyan.face.domain.dto;

import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

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
    private String groupId;

    // 指定时为搜索,不指定则返回全部
    private String userId;

    private Integer startIndex;
    private Integer length;
}
