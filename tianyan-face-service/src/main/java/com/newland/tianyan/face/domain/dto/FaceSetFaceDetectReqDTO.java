package com.newland.tianyan.face.domain.dto;

import lombok.Data;
import org.hibernate.validator.constraints.Range;

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
    @Min(1L)
    private Long appId;

    @NotBlank
    private String image;

    @Range(min = 1,max = 120)
    private Integer maxFaceNum = 1;

    private String faceFields;

    private Integer qualityControl;
}
