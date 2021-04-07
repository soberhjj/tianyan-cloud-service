package com.newland.tianyan.face.domain.vo;

import lombok.Builder;
import lombok.Data;

/**
 * @author: RojiaHuang
 * @description:
 * @date: 2021/4/6
 */
@Builder
@Data
public class FaceSearchVo {
    private Long vectorId;

    private Long appId;

    private Long gid;

    private Long uid;

    private String distance;
}
