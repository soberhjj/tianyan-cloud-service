package com.newland.tianyan.face.domain.dto;

import com.newland.tianyan.face.constant.VerifyConstant;
import lombok.Builder;
import lombok.Data;

import javax.validation.constraints.*;

import static com.newland.tianyan.face.constant.BusinessArgumentConstants.*;

@Data
public class FaceSetGroupGetListReqDTO {

    @NotBlank
    private String account;

    @NotNull
    @Min(1599613749000L)
    private Long appId;

    @Pattern(regexp = VerifyConstant.GROUP_ID_OLD)
    private String groupId;

    /**
     * 查询起始位置，默认为 0
     */
    @Min(0)
    private Integer startIndex = DEFAULT_PAGE_INDEX;

    /**
     * 查询个数，默认为 100
     */
    @Min(0)
    @Max(1000)
    private Integer length = DEFAULT_PAGE_SIZE;
}
