package com.newland.tianyan.common.log;

import com.newland.tianyan.common.utils.NetworkUtils;
import org.slf4j.MDC;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;
import java.net.UnknownHostException;

/**
 * @author: RojiaHuang
 * @description:
 * @date: 2021/2/25
 */
@Component
public class LogFixColumnsHelper {

    public void init(HttpServletRequest request, String serverAddress) throws UnknownHostException {
        String url = request.getRequestURI();
        String clientIp = NetworkUtils.getClientIpAddress(request);
        MDC.put("uri", url);
        MDC.put("responseId", serverAddress);
        MDC.put("requestIp", clientIp);
    }

    public void clear() {
        MDC.remove("uri");
        MDC.remove("responseId");
        MDC.remove("requestIp");
    }
}
