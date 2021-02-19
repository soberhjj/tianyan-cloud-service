package com.newland.tianyan.vectorsearch.vo;

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
public class BatchQueryReq {

    private String appId;
    private List<List<Float>> features;
    private Integer topK;

}
