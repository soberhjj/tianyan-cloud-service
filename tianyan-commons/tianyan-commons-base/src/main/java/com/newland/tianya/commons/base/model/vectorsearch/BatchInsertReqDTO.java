package com.newland.tianya.commons.base.model.vectorsearch;

import lombok.*;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import java.util.List;

/**
 * @author: RojiaHuang
 * @description:
 * @date: 2021/2/8
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BatchInsertReqDTO {

    @NotBlank
    private String appId;

    @NotEmpty
    private List<List<Float>> features;

    @NotEmpty
    private List<Long> entityIds;

}
