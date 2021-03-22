package com.newland.tianyan.gateway.constant;

/**
 * 错误码枚举类
 * @author sj
 *
 */
public enum ErrorCodeEnum {
    /**
     * QPS超限额
     */
    QPS_OVER_QUOTA(6202, "Open api qps request limit reached")
    ;
	
	private ErrorCodeEnum(int errorCode, String errorMsg) {
        this.errorCode = errorCode;
        this.errorMsg = errorMsg;
    }
	
    private int errorCode;

    private String errorMsg;

    public int getErrorCode() {
        return errorCode;
    }

    public String getErrorMsg() {
        return errorMsg;
    }

}
