package com.newland.tianyan.face.dto.face;

import io.swagger.annotations.ApiModel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;

/**
 * @author: RojiaHuang
 * @description:
 * @date: 2021/2/5
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@ApiModel
public class BackendFacesetFaceDetectRequest {
    @NotBlank
    private String account;

    @NotBlank
    private String image;

    @Min(1)
    private int maxFaceNum = 1;

    private String faceFields;

    private int qualityControl;

}
