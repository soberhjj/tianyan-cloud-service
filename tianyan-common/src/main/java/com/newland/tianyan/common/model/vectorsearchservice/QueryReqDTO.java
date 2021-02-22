package com.newland.tianyan.common.model.vectorsearchservice;

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
public class QueryReqDTO {

    private String appId;
    private List<Float> feature;
    private Integer topK;

}
