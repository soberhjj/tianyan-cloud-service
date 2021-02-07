package com.newland.tianyan.vectorsearch.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @Author: huangJunJie  2021-01-13 10:15
 */

@Data
@NoArgsConstructor
@AllArgsConstructor
public class QueryRes {
    Long entityId;
    Float distance;
}
