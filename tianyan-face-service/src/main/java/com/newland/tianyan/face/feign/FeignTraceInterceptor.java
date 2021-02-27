package com.newland.tianyan.face.feign;

import feign.RequestInterceptor;
import feign.RequestTemplate;
import org.slf4j.MDC;
import org.springframework.util.StringUtils;

import static com.newland.tianyan.common.constans.GlobalTraceConstant.GATEWAY_TRACE_HEAD;
import static com.newland.tianyan.common.constans.GlobalTraceConstant.TRACE_MDC;

/**
 * @author: RojiaHuang
 * @description:
 * @date: 2021/2/26
 */
public class FeignTraceInterceptor implements RequestInterceptor {
    @Override
    public void apply(RequestTemplate requestTemplate) {
        //获取traceId
        //String gatewayTrace = TraceContext.traceId();
        String gatewayTrace = MDC.get(TRACE_MDC);
        if (!StringUtils.isEmpty(gatewayTrace)) {
            requestTemplate.header(GATEWAY_TRACE_HEAD, gatewayTrace);
        }
    }
}
