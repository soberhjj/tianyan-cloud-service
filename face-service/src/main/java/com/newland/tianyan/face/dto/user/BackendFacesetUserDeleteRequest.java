package com.newland.tianyan.face.dto.user;

import io.swagger.annotations.ApiModel;
import lombok.*;

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
public class BackendFacesetUserDeleteRequest {
    @NotBlank
    private String account;

    @Min(0)
    @NonNull
    private Long appId;

    @NotBlank
    private String groupId;

    @NotBlank
    private String userId;
}
