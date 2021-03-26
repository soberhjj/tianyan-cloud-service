package com.newland.tianya.commons.base.model;

import com.alibaba.fastjson.annotation.JSONField;
import com.newland.tianya.commons.base.utils.LogIdUtils;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

/**
 * @author Administrator
 */
@Data
@Builder
@AllArgsConstructor
public class JsonErrorObject {

    @JSONField(name = "log_id")
    private String logId;

    @JSONField(name = "error_code")
    private int errorCode;

    @JSONField(name = "error_msg")
    private String errorMsg;

    public JsonErrorObject(int errorCode, String errorMsg) {
        this.logId = LogIdUtils.traceId();
        this.errorCode = errorCode;
        this.errorMsg = errorMsg;
    }
}
