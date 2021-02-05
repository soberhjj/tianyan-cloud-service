package com.newland.tianyan.face.dto.app;

import io.swagger.annotations.ApiModel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.Range;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
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
public class BackendAppCreateRequest {
    @NotEmpty
    private String account;

    @NotEmpty
    @Pattern(regexp = "^[\\dA-Za-z_\\u4e00-\\u9fa5]{0,32}$")
    private String appName;

    @Pattern(regexp = "^[1-5,]{1,9}$")
    private String apiList;

    @NotEmpty
    private String appInfo;

    @NotNull
    @Range(min = 1, max = 3)
    private Integer type;
}
