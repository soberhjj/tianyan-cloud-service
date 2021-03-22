package com.newland.tianyan.face.domain.dto;

import lombok.Data;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;

@Data
public class AppGetListReqDTO {

    /**
     * 用户账号
     */
    @NotBlank
    private String account;

    /**
     * 应⽤名称，若指定，则为搜索该应⽤
     */
    private String appName;

    /**
     * 查询起始位置，默认为 0
     */
    @Min(0)
    private Integer startIndex;

    /**
     * 查询个数，默认为 100
     */
    @Min(0)
    private Integer length;
}
