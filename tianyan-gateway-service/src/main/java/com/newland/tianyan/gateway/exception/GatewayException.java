package com.newland.tianyan.gateway.exception;

import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * @author: RojiaHuang
 * @description:
 * @date: 2021/3/6
 */
@Data
@AllArgsConstructor
public class GatewayException extends RuntimeException{

    private int errorCode;

    private String errorMsg;
}
