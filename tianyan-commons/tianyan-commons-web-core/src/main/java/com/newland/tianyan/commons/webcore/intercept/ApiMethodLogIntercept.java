package com.newland.tianyan.commons.webcore.intercept;

import com.newland.tianya.commons.base.utils.LogFixColumnUtils;
import com.newland.tianya.commons.base.utils.ServerAddressUtils;
import com.newland.tianyan.commons.webcore.utils.NetworkUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;


import javax.annotation.Nullable;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.time.Duration;
import java.time.LocalDateTime;

import static com.newland.tianya.commons.base.constants.GlobalLogConstant.GATEWAY_TRACE_HEAD;
import static com.newland.tianya.commons.base.constants.TokenConstants.HEAD_ACCOUNT;
import static com.newland.tianya.commons.base.constants.TokenConstants.HEAD_APP_ID;

/**
 * @author: RojiaHuang
 * @description:
 * @date: 2021/2/25
 */
@Slf4j
public class ApiMethodLogIntercept implements HandlerInterceptor {
    private final ServerAddressUtils serverAddressUtils;

    public ApiMethodLogIntercept(ServerAddressUtils serverAddressUtils) {
        this.serverAddressUtils = serverAddressUtils;
    }

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        //定义固定列
        LogFixColumnUtils.init(LogFixColumnUtils.LogFixColumn.builder()
                .url(request.getRequestURI())
                .clientIp(NetworkUtils.getClientIpAddress(request))
                .serverAddress(serverAddressUtils.getServerAddress())
                .traceId(request.getHeader(GATEWAY_TRACE_HEAD))
                .account(request.getHeader(HEAD_ACCOUNT))
                .appId(request.getHeader(HEAD_APP_ID))
                .build());
        //输出请求时间
        String requestTime = LocalDateTime.now().toString();
        request.setAttribute("requestTime", requestTime);
        return true;
    }

    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, @Nullable ModelAndView modelAndView) {
        //输出响应时间
        String requestTime = (String) request.getAttribute("requestTime");
        LocalDateTime now = LocalDateTime.now();
        String responseTime = now.toString();
        double consuming = Duration.between(LocalDateTime.parse(requestTime), now).getNano() / 1000000.0;
        log.info("requestTime:{},responseTime:{},consuming:{}ms", requestTime, responseTime, consuming);
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, @Nullable Exception ex) throws Exception {
        //logFixColumnUtils.clear();
    }
}
