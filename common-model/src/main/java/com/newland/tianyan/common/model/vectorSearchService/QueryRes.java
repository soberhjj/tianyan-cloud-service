package com.newland.tianyan.common.model.vectorSearchService;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author: RojiaHuang
 * @description:
 * @date: 2021/2/8
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class QueryRes {
    Long entityId;
    Float distance;

    Long uid;
    Long gid;
}
