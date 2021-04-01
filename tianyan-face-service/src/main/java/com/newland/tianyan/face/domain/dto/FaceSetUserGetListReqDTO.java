package com.newland.tianyan.face.domain.dto;

import com.newland.tianyan.face.constant.VerifyConstant;
import lombok.Builder;
import lombok.Data;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;

import static com.newland.tianyan.face.constant.BusinessArgumentConstants.*;

@Data
public class FaceSetUserGetListReqDTO {

    @NotBlank
    private String account;

    @NotNull
    @Min(1599613749000L)
    private Long appId;

    @NotBlank
    @Pattern(regexp = VerifyConstant.GROUP_ID_OLD)
    private String groupId;

    @Pattern(regexp = VerifyConstant.USER_ID_OLD)
    private String userId;

    /**
     * 查询起始位置，默认为 0
     */
    @Min(0)
    private Integer startIndex = DEFAULT_PAGE_INDEX;

    /**
     * 查询个数，默认为 100
     */
    @Min(0)
    private Integer length = DEFAULT_PAGE_SIZE;
}
