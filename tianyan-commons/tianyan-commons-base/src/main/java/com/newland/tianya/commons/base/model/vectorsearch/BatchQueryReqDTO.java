package com.newland.tianya.commons.base.model.vectorsearch;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

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
public class BatchQueryReqDTO {

    private String appId;
    private List<List<Float>> features;
    private Integer topK;

}
