package com.newland.tianyan.face.domain.dto;

import lombok.Data;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;

/**
 * @program: newland-cloud
 * @description:
 * @author: THE KING
 **/
@Data
public class FaceDetectReqDTO {

    @NotBlank
    private String image;

    @Min(1)
    @Max(20)
    private Integer maxFaceNum = 1;

    private String faceFields;


}
