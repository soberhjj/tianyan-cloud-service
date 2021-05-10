package com.newland.tianyan.face.domain.dto;

import com.newland.tianyan.face.validate.FaceFieldValid;
import lombok.Data;
import org.hibernate.validator.constraints.Range;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

import static com.newland.tianyan.face.constant.BusinessArgumentConstants.FACE_FIELD_COORDINATE;

/**
 * @program: newland-cloud
 * @description:
 * @author: THE KING
 **/
@Data
public class FaceInteractLiveNessReqDTO {

    @NotNull
    private Long appId;

    @NotBlank
    private String account;

    @NotBlank
    private String video;

    @Min(1)
    private int maxFaceNum = 1;

    @Min(1)
    private int maxUserNum = 1;

    private int interLiveAction;


}
