package com.newland.tianyan.face.domain.dto;

import lombok.Data;
import org.hibernate.validator.constraints.Range;

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

    @Range(min = 1, max = 120)
    private Integer maxFaceNum = 1;

    private String faceFields;


}
