package com.newland.tianyan.face.domain.dto;

import com.newland.tianyan.face.validate.FaceFieldValid;
import lombok.Data;
import org.hibernate.validator.constraints.Range;

import javax.validation.constraints.NotBlank;

import static com.newland.tianyan.face.constant.BusinessArgumentConstants.FACE_FIELD_COORDINATE;

/**
 * @program: newland-cloud
 * @description:
 * @author: THE KING
 **/
@Data
public class FaceLiveNessReqDTO {

    @NotBlank
    private String image;

    @Range(min = 1, max = 120)
    private Integer maxFaceNum = 1;

    @FaceFieldValid(benchmark = FACE_FIELD_COORDINATE)
    private String faceFields;


}
