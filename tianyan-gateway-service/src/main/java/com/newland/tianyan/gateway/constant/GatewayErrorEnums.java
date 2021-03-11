package com.newland.tianyan.gateway.constant;

import com.newland.tianyan.gateway.exception.GatewayException;
import lombok.AllArgsConstructor;

/**
 * @author: RojiaHuang
 * @description:
 * @date: 2021/3/6
 */
@AllArgsConstructor
public enum GatewayErrorEnums {
    /**
     * 测试异常
     * */
    DEMO(10010,"test gateway exception"),
    NO_TOKEN(6100,"No Access token"),
    TOKEN_INVALID(6101,"Access token invalid"),
    TOKEN_EXPIRED(6102,"Access token expired")
    ;
    private int errorCode;

    private String errorMsg;

    public int getErrorCode() {
        return errorCode;
    }

    public String getErrorMsg() {
        return errorMsg;
    }

    public GatewayException toException() {
        return new GatewayException(this.getErrorCode(), this.getErrorMsg());
    }
}
