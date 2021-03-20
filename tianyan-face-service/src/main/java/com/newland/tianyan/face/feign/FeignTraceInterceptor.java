package com.newland.tianyan.face.feign;

import feign.RequestInterceptor;
import feign.RequestTemplate;
import org.slf4j.MDC;
import org.springframework.util.StringUtils;

import static com.newland.tianya.commons.base.constants.GlobalTraceConstant.GATEWAY_TRACE_HEAD;


/**
 * @author: RojiaHuang
 * @description:
 * @date: 2021/2/26
 */
public class FeignTraceInterceptor implements RequestInterceptor {
    @Override
    public void apply(RequestTemplate requestTemplate) {
        //获取traceId
        String gatewayTrace = MDC.get("traceId");
        if (!StringUtils.isEmpty(gatewayTrace)) {
            requestTemplate.header(GATEWAY_TRACE_HEAD, gatewayTrace);
        }
    }
}
