package com.newland.tianyan.face.domain.dto;

import lombok.Data;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

import static com.newland.tianyan.face.constant.BusinessArgumentConstants.MIN_APP_ID;

/**
 * @program: newland-cloud
 * @description:
 * @author: THE KING
 **/
@Data
public class FaceSetFaceDetectReqDTO {
    @NotBlank
    private String account;

    @NotNull
    @Min(1599613749000L)
    private Long appId;

    @NotBlank
    private String image;

    @Min(1)
    @Max(20)
    private Integer maxFaceNum = 1;

    private String faceFields;

    private int qualityControl;

}
