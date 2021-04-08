package com.newland.tianyan.face.domain.dto;

import lombok.Data;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;

import static com.newland.tianyan.face.constant.BusinessArgumentConstants.DEFAULT_PAGE_INDEX;
import static com.newland.tianyan.face.constant.BusinessArgumentConstants.DEFAULT_PAGE_SIZE;

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
    private Integer startIndex = DEFAULT_PAGE_INDEX;

    /**
     * 查询个数，默认为 100
     */
    @Min(0)
    private Integer length = DEFAULT_PAGE_SIZE;
}
