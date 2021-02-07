package com.newland.tianyan.common.feign.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class MilvusQueryRes {

    Long entityId;

    Float distance;

    Long gid;

    Long uid;
}