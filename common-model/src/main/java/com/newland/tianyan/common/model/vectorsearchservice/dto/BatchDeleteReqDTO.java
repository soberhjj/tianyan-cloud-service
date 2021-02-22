package com.newland.tianyan.common.model.vectorsearchservice.dto;

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
public class BatchDeleteReqDTO {

    private String appId;
    private List<Long> entityIds;

}