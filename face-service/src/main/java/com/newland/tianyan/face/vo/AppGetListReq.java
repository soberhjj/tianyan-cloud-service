package com.newland.tianyan.face.vo;

import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;

@Getter
@Setter
public class AppGetListReq {

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
