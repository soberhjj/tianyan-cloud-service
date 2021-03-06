//package com.newland.tianyan.auth.filter;
//
//import com.newland.tianyan.common.utils.JsonUtils;
//import lombok.extern.slf4j.Slf4j;
//import org.apache.commons.lang3.StringUtils;
//import org.json.JSONObject;
//import org.slf4j.MDC;
//import org.springframework.beans.factory.annotation.Value;
//import org.springframework.stereotype.Component;
//import org.springframework.web.filter.AbstractRequestLoggingFilter;
//
//import javax.servlet.http.HttpServletRequest;
//
///**
// * @author: RojiaHuang
// * @description:
// * @date: 2021/2/23
// */
//@Component
//@Slf4j
//public class ApiRequestLoggingFilter extends AbstractRequestLoggingFilter {
//    @Override
//    protected boolean shouldLog(HttpServletRequest request) {
//        return super.shouldLog(request);
//    }
//
//    @Value("${spring.application.name}")
//    String applicationName;
//
//    @Override
//    protected void beforeRequest(HttpServletRequest request, String s) {
//        String url = request.getRequestURI();
//        MDC.put("server-name", applicationName);
//
//        log.info("request.url:{},request.param:{}", url, JsonUtils.toJson(request.getParameterMap()));
//    }
//
//    @Override
//    protected void afterRequest(HttpServletRequest request, String s) {
//        MDC.remove("server-name");
//    }
//}
