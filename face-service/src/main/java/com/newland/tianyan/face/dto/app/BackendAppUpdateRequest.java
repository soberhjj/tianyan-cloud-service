package com.newland.tianyan.face.dto.app;

import io.swagger.annotations.ApiModel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Pattern;

/**
 * @author: RojiaHuang
 * @description:
 * @date: 2021/2/4
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@ApiModel
public class BackendAppUpdateRequest {
    @NotEmpty
    private String account;

    @NotEmpty
    @Pattern(regexp = "^[\\dA-Za-z_\\u4e00-\\u9fa5]{0,32}$")
    private String appName;

    @Pattern(regexp = "^[1-5,]{1,9}$")
    private String apiList;

    @NotEmpty
    private String appInfo;

    @Min(1)
    private Long appId;
}
