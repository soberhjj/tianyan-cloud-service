package com.newland.tianyan.face.vo;

import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotBlank;

/**
 * @program: newland-cloud
 * @description:
 * @author: THE KING
 **/
@Getter
@Setter
public class FaceSetFaceCompareReq {

    @NotBlank
    private String firstImage;

    @NotBlank
    private String secondImage;

    private String faceFields;

}
