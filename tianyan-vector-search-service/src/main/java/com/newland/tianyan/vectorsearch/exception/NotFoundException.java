package com.newland.tianyan.vectorsearch.exception;

import com.newland.tianyan.common.constans.GlobalArgumentErrorEnums;
import com.newland.tianyan.common.exception.ArgumentException;
import org.slf4j.MDC;
import org.springframework.boot.web.servlet.error.ErrorController;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.Map;

/**
 * @author: RojiaHuang
 * @description:
 * @date: 2021/3/6
 */
@Controller
public class NotFoundException implements ErrorController {

    @Override
    public String getErrorPath() {
        return "/error";
    }

    @RequestMapping(value = {"/error"})
    @ResponseBody
    public Object error(HttpServletRequest request) {
        Map<String, Object> body = new HashMap<>();
        ArgumentException exception = GlobalArgumentErrorEnums.INVALID_METHOD.toException();
        body.put("logId", MDC.get("traceId"));
        body.put("errorCode", exception.getErrorCode());
        body.put("errorMsg", exception.getErrorMsg());
        return body;
    }
}
