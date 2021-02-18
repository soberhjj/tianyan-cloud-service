package com.newland.tianyan.common.feign.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author: RojiaHuang
 * @description:
 * @date: 2021/2/9
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ParamDto {
    String name;
}
