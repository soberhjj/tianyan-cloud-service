package com.newland.tianyan.face.domain.dto;

import lombok.Data;

import javax.validation.constraints.*;

/**
 * @program: newland-cloud
 * @description:
 * @author: THE KING
 **/
@Data
public class FaceSetFaceSearchReqDTO {

    @NotNull
    private Long appId;

    @NotBlank
    private String account;

    @NotBlank
    private String image;

    @NotBlank
    @Pattern(regexp = "^[\\dA-Za-z_\\u4e00-\\u9fa5,]{0,32}$")
    private String groupId;

    @Pattern(regexp = "^[\\dA-Za-z_\\u4e00-\\u9fa5]{0,32}$")
    private String userId;

    @Min(1)
    @Max(120)
    private int maxFaceNum = 1;

    @Min(1)
    @Max(50)
    private int maxUserNum = 1;

    private String faceFields;

    private String deviceId;


}
