package com.newland.tianyan.face.domain.dto;

import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;

@Getter
@Setter
public class FaceSetGetListReqDTO {

    @NotBlank
    private String account;

    // 指定时为搜索,不指定则返回全部
    private String appName;

    @Min(0)
    private int startIndex;

    @Min(0)
    private int length;
}
