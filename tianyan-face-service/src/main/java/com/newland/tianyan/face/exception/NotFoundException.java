package com.newland.tianyan.face.exception;

import com.newland.tianyan.common.constans.GlobalSystemErrorEnums;
import com.newland.tianyan.common.exception.SysException;
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
        SysException sysException = GlobalSystemErrorEnums.INVALID_METHOD.toException();
        body.put("logId", MDC.get("traceId"));
        body.put("errorCode", sysException.getErrorCode());
        body.put("errorMsg", sysException.getErrorMsg());
        return body;
    }
}
