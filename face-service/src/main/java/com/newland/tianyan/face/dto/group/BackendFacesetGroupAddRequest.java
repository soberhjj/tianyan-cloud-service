package com.newland.tianyan.face.dto.group;

import io.swagger.annotations.ApiModel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;

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
public class BackendFacesetGroupAddRequest {

    @NotEmpty
    @Pattern(regexp = "^[\\dA-Za-z_\\u4e00-\\u9fa5]{0,32}$")
    private String groupId;

    @NotNull
    private Long appId;

    @NotBlank
    private String account;
}
