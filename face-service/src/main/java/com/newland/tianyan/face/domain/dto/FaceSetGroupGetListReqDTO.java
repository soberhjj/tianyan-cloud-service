package com.newland.tianyan.face.domain.dto;

import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Getter
@Setter
public class FaceSetGroupGetListReqDTO {

    @NotBlank
    private String account;

    @NotNull
    @Min(1)
    private Long appId;

    // 指定时为搜索,不指定则返回全部
    private String groupId;

    @Min(0)
    private int startIndex;

    @Min(0)
    private int length;
}
