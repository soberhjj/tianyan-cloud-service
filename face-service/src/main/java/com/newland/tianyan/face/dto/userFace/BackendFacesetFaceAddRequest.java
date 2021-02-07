package com.newland.tianyan.face.dto.userFace;

import io.swagger.annotations.ApiModel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;
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
public class BackendFacesetFaceAddRequest {

    @NotBlank
    private String image;

    @NotBlank
    @Pattern(regexp = "^[\\dA-Za-z_\\u4e00-\\u9fa5]{0,32}$")
    private String userId;

    @Pattern(regexp = "^[\\dA-Za-z_\\u4e00-\\u9fa5]{0,32}$")
    private String userName;

    @NotBlank
    private String groupId;

    @NotNull
    private long appId;

    @NotBlank
    private String account;

    private String userInfo;
    /**
     * action_type取值有两种("append"和"replace")。
     * */
    private String actionType;

    private int type;

    private int qualityControl;
}
