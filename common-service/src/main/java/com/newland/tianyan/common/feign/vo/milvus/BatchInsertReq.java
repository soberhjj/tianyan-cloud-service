package com.newland.tianyan.common.feign.vo.milvus;

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
public class BatchInsertReq {

    private String appId;
    private List<List<Float>> features;
    private List<Long> entityIds;

}
