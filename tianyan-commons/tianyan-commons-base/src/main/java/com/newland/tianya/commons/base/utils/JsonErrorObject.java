package com.newland.tianya.commons.base.utils;

public class JsonErrorObject {

    private String logId;
    private int errorCode;
    private String errorMsg;

    public JsonErrorObject(String logId, int errorCode, String errorMsg) {
        this.logId = logId;
        this.errorCode = errorCode;
        this.errorMsg = errorMsg;
    }

    public String getLogId() {
        return logId;
    }

    public void setLogId(String logId) {
        this.logId = logId;
    }

    public int getErrorCode() {
        return errorCode;
    }

    public void setErrorCode(int errorCode) {
        this.errorCode = errorCode;
    }

    public String getErrorMsg() {
        return errorMsg;
    }

    public void setErrorMsg(String errorMsg) {
        this.errorMsg = errorMsg;
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("{");
        sb.append("\"error_code\":").append(errorCode);
        sb.append(",\"error_msg\":\"").append(errorMsg).append('\"');
        sb.append('}');
        return sb.toString();
    }
}
