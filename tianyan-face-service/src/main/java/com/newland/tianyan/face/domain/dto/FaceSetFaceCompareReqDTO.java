package com.newland.tianyan.face.domain.dto;

import lombok.Data;

import javax.validation.constraints.NotBlank;

/**
 * @program: newland-cloud
 * @description:
 * @author: THE KING
 **/
@Data
public class FaceSetFaceCompareReqDTO {

    @NotBlank
    private String firstImage;

    @NotBlank
    private String secondImage;

    private String faceFields;

}
