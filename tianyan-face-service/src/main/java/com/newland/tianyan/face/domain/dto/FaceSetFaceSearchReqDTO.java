package com.newland.tianyan.face.domain.dto;

import com.newland.tianyan.face.constant.VerifyConstant;
import com.newland.tianyan.face.validate.FaceFieldValid;
import lombok.Data;
import org.hibernate.validator.constraints.Range;

import javax.validation.constraints.*;

/**
 * @program: newland-cloud
 * @description:
 * @author: THE KING
 **/
@Data
public class FaceSetFaceSearchReqDTO {

    @NotBlank
    private String account;

    @NotNull
    @Min(1L)
    private Long appId;

    @NotBlank
    private String image;

    @NotBlank
    @Pattern(regexp = VerifyConstant.GROUP_ID_LIST)
    private String groupId;

    @Pattern(regexp = VerifyConstant.USER_ID_OLD)
    private String userId;

    @Range(min = 1, max = 1)
    private Integer maxFaceNum = 1;

    @Range(min = 1, max = 20)
    private Integer maxUserNum = 1;

    @FaceFieldValid
    private String faceFields;

    private String deviceId;


}
