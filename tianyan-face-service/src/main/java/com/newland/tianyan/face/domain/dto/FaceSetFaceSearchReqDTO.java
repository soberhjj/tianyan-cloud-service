package com.newland.tianyan.face.domain.dto;

import com.newland.tianyan.face.constant.VerifyConstant;
import lombok.Data;

import javax.validation.constraints.*;

import static com.newland.tianyan.face.constant.BusinessArgumentConstants.MAX_FACE_NUMBER;
import static com.newland.tianyan.face.constant.BusinessArgumentConstants.MIN_APP_ID;

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
    @Min(1599613749000L)
    private Long appId;

    @NotBlank
    private String image;

    @NotBlank
    @Pattern(regexp = VerifyConstant.GROUP_ID_LIST)
    private String groupId;

    @Pattern(regexp = VerifyConstant.USER_ID_OLD)
    private String userId;

    @Min(1)
    @Max(1)
    private int maxFaceNum = 1;

    @Min(1)
    @Max(20)
    private int maxUserNum = 1;

    private String faceFields;

    private String deviceId;


}
