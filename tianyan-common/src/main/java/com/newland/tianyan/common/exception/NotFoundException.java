package com.newland.tianyan.common.exception;

import com.newland.tianyan.common.constans.GlobalArgumentErrorEnums;
import org.slf4j.MDC;
import org.springframework.boot.autoconfigure.web.ErrorProperties;
import org.springframework.boot.autoconfigure.web.servlet.error.BasicErrorController;
import org.springframework.boot.web.servlet.error.ErrorAttributes;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.Map;

/**
 * @author: RojiaHuang
 * @description:
 * @date: 2021/3/6
 */
@Controller
public class NotFoundException extends BasicErrorController {

    public NotFoundException(ErrorAttributes errorAttributes) {
        super(errorAttributes, new ErrorProperties());
    }

    @Override
    @RequestMapping
    public ResponseEntity<Map<String, Object>> error(HttpServletRequest httpServletRequest) {
        Map<String, Object> body = new HashMap<>();
        ArgumentException exception = GlobalArgumentErrorEnums.INVALID_METHOD.toException();
        body.put("logId", MDC.get("traceId"));
        body.put("errorCode", exception.getErrorCode());
        body.put("errorMsg", exception.getErrorMsg());
        return new ResponseEntity<>(
                body
                , HttpStatus.INTERNAL_SERVER_ERROR
        );
    }
}
