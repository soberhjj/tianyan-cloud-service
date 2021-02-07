package com.newland.tianyan.face.dto.user;

import io.swagger.annotations.ApiModel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

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
public class BackendFacesetUserCopyRequest {

    @NotBlank
    private String account;

    @NotBlank
    private String userId;

    @NotNull
    private Long appId;

    @NotBlank
    private String dstGroupId;

    @NotBlank
    private String srcGroupId;

}
