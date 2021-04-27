package com.newland.tianyan.face.domain.dto;

import com.newland.tianyan.face.validate.ActionTypeValid;
import com.newland.tianyan.face.constant.VerifyConstant;
import com.newland.tianyan.face.validate.QualityControlValid;
import lombok.Data;
import org.hibernate.validator.constraints.Range;

import javax.validation.constraints.*;

@Data
public class FaceSetFaceAddReqDTO {

    @NotBlank
    private String account;

    @NotNull
    @Min(1L)
    private Long appId;

    @NotBlank
    private String image;

    @NotBlank
    @Pattern(regexp = VerifyConstant.USER_ID)
    private String userId;

    @Pattern(regexp = VerifyConstant.USER_NAME)
    private String userName;

    @NotBlank
    @Pattern(regexp = VerifyConstant.GROUP_ID)
    private String groupId;

    @Pattern(regexp = VerifyConstant.USER_INFO)
    private String userInfo;

    @ActionTypeValid
    private String actionType;

    private Integer type;

    @QualityControlValid
    private Integer qualityControl;
}
