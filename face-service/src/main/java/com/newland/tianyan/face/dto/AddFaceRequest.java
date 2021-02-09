package com.newland.tianyan.face.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * @author: RojiaHuang
 * @description:
 * @date: 2021/1/8
 */
@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class AddFaceRequest {

    private List<Float> feature;

    private Long faceId;

    private Long gid;

    private Long uid;
}
