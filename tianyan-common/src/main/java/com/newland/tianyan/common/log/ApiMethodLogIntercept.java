package com.newland.tianyan.common.log;

import brave.internal.Nullable;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.time.LocalDateTime;

/**
 * @author: RojiaHuang
 * @description:
 * @date: 2021/2/25
 */
@Slf4j
public class ApiMethodLogIntercept implements HandlerInterceptor{
    private final LogFixColumnsHelper logFixColumnsHelper;
    private final ServerAddressHelper serverAddressHelper;

    public ApiMethodLogIntercept(LogFixColumnsHelper logFixColumnsHelper, ServerAddressHelper serverAddressHelper) {
        this.logFixColumnsHelper = logFixColumnsHelper;
        this.serverAddressHelper = serverAddressHelper;
    }

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        //定义固定列
        logFixColumnsHelper.init(request, serverAddressHelper.getServerAddress());
        //输出请求时间
        String requestTime = LocalDateTime.now().toString();
        request.setAttribute("requestTime", requestTime);
        log.info("requestTime:{}", requestTime);
        return true;
    }

    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, @Nullable ModelAndView modelAndView) throws Exception {
        //输出响应时间
        String requestTime = (String) request.getAttribute("requestTime");
        String responseTime = LocalDateTime.now().toString();
        log.info("requestTime:{},responseTime:{}", requestTime, responseTime);
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, @Nullable Exception ex) throws Exception {
        logFixColumnsHelper.clear();
    }
}
