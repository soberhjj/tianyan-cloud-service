package com.newland.tianya.commons.base.model;

import com.alibaba.fastjson.annotation.JSONField;

import lombok.Data;

/**
 *       异常或错误弘一返回模型
 * @author sj
 *
 */
@Data
public class ErrorResponseDTO {

	@JSONField(name = "log_id")
	private String logId;
	
	@JSONField(name = "error_code")
	private int errorCode;
	
	@JSONField(name = "error_msg")
	private String errorMsg;
}
