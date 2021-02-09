package com.newland.tianyan.face.privateBean;

import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

/**
 * @program: newland-cloud
 * @description:
 * @author: THE KING
 **/
@Getter
@Setter
public class BackendFacesetFaceSearchRequest {

    @NotNull
    private Long appId;

    @NotBlank
    private String account;

    @NotBlank
    private String image;

    @NotBlank
    private String groupId;

    private String userId;

    @Min(1)
    private int maxFaceNum = 1;

    @Min(1)
    private int maxUserNum = 1;

    private String faceFields;

    private String deviceId;


}
