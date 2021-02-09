package com.newland.tianyan.face.privateBean;

import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;

/**
 * @program: newland-cloud
 * @description:
 * @author: THE KING
 **/
@Getter
@Setter
public class FaceDetectVo {

    @NotBlank
    private String image;

    @Min(1)
    private int maxFaceNum = 1;

    private String faceFields;


}
