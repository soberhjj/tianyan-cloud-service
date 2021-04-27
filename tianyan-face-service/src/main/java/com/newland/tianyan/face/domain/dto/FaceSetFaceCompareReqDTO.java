package com.newland.tianyan.face.domain.dto;

import com.newland.tianyan.face.validate.FaceFieldValid;
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

    @FaceFieldValid
    private String faceFields;

}
