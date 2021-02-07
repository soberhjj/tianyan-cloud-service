package com.newland.tianyan.face.dto.app;

import io.swagger.annotations.ApiModel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

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
public class BackendAppDeleteRequest {

    @NotEmpty
    private String account;

    @NotNull
    @Min(1)
    private Long appId;

}
