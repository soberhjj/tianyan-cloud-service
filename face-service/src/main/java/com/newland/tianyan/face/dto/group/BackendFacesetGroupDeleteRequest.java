package com.newland.tianyan.face.dto.group;

import io.swagger.annotations.ApiModel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;

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
public class BackendFacesetGroupDeleteRequest {

    @NotBlank
    private String account;

    @Min(0)
    private Long appId;

    @NotBlank
    private String groupId;
}
