package com.newland.tianyan.face.domain.dto;

import com.newland.tianyan.face.constant.VerifyConstant;
import lombok.Data;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.*;

import static com.newland.tianyan.face.constant.BusinessArgumentConstants.MIN_APP_ID;

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
    /**
     * action_type取值有两种("append"和"replace")。
     */
    private String actionType;

    private Integer type;

    private Integer qualityControl;
}
