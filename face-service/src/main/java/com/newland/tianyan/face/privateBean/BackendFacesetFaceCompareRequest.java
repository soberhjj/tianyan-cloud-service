package com.newland.tianyan.face.privateBean;

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
public class BackendFacesetFaceCompareRequest {

    @NotBlank
    private String firstImage;

    @NotBlank
    private String secondImage;

    private String faceFields;

}
