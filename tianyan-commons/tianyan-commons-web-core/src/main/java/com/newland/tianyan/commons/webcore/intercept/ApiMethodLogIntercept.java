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
import java.time.LocalDateTime;

import static com.newland.tianya.commons.base.constants.GlobalLogConstant.GATEWAY_TRACE_HEAD;

/**
 * @author: RojiaHuang
 * @description:
 * @date: 2021/2/25
 */
@Slf4j
public class ApiMethodLogIntercept implements HandlerInterceptor {
    private final LogFixColumnUtils logFixColumnUtils;
    private final ServerAddressUtils serverAddressUtils;

    public ApiMethodLogIntercept(LogFixColumnUtils logFixColumnUtils, ServerAddressUtils serverAddressUtils) {
        this.logFixColumnUtils = logFixColumnUtils;
        this.serverAddressUtils = serverAddressUtils;
    }

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        //定义固定列
        String url = request.getRequestURI();
        String requestIp = NetworkUtils.getClientIpAddress(request);
        String responseIp = serverAddressUtils.getServerAddress();
        String traceId = request.getHeader(GATEWAY_TRACE_HEAD);
        logFixColumnUtils.init(traceId, url, requestIp, responseIp);
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
        //logFixColumnUtils.clear();
    }
}
