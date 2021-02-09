package com.newland.tianyan.face.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author: RojiaHuang
 * @description:
 * @date: 2021/1/8
 */
@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class SearchFaceResponse {

    private Long gid;

    private Long uid;

    private Float confidence;

}
