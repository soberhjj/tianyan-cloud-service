package com.newland.tianyan.common.feign.dto.image;

import lombok.AllArgsConstructor;
import lombok.Builder;
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
@Builder
public class UploadReq {

    String image;

}
