package com.newland.tianyan.face.dto.userFace;

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
public class BackendFacesetUserFaceGetListRequest {
    @NotBlank
    private String account;

    @NotNull
    private Long appId;

    @NotBlank
    private String groupId;

    @NotBlank
    private String userId;

    private int startIndex;
    private int length;
}
