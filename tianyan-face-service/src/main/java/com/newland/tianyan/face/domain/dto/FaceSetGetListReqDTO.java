package com.newland.tianyan.face.domain.dto;

import lombok.Data;
import org.hibernate.validator.constraints.Range;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;

@Data
public class FaceSetGetListReqDTO {

    @NotBlank
    private String account;

    // 指定时为搜索,不指定则返回全部
    private String appName;

    @Min(0)
    private int startIndex;

    @Range(min = 1,max = 1000)
    private int length;
}
