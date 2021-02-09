package com.newland.tianyan.vectorsearch.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * @Author: huangJunJie  2021-02-04 16:29
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class QueryReq {

    private String appId;
    private List<Float> feature;
    private Integer topK;

}
