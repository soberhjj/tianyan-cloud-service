package com.newland.tianyan.face.dto.user;

import io.swagger.annotations.ApiModel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

/**
 * @author: RojiaHuang
 * @description:
 * @date: 2021/2/5
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@ApiModel
public class BackendFacesetUserGetListRequest {

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
