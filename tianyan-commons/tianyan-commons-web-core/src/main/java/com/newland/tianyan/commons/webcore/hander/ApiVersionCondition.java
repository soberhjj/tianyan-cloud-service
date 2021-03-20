package com.newland.tianyan.commons.webcore.hander;

import com.newland.tianya.commons.base.constants.GlobalArgumentErrorEnums;
import lombok.Data;
import org.springframework.web.servlet.mvc.condition.RequestCondition;

import javax.servlet.http.HttpServletRequest;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author: https://mp.weixin.qq.com/s/m2HnUBXagKaLQjzww1s77g
 * @description: 版本号匹配筛选条件
 * @date: 2021/2/22
 */
@Data
public class ApiVersionCondition implements RequestCondition<ApiVersionCondition> {
    /**
     * 接口路径中的版本号前缀，如: api/v[1-n]/fun
     */
    private final static Pattern VERSION_PREFIX = Pattern.compile("/v\\d+");
    private Integer apiVersion;

    ApiVersionCondition(Integer apiVersion) {
        this.apiVersion = apiVersion;
    }

    /**
     * 最近优先原则，方法定义的 @ApiVersion > 类定义的 @ApiVersion
     */
    @Override
    public ApiVersionCondition combine(ApiVersionCondition other) {
        return new ApiVersionCondition(other.getApiVersion());
    }

    /**
     * 获得符合匹配条件的ApiVersionCondition
     */
    @Override
    public ApiVersionCondition getMatchingCondition(HttpServletRequest request) {
        Matcher m = VERSION_PREFIX.matcher(request.getRequestURI());
        if (m.find()) {
            String result = m.group(0).split("v")[1];
            int version = Integer.parseInt(result);
            if (version >= getApiVersion()) {
                return this;
            }
        }
        throw GlobalArgumentErrorEnums.SERVICE_NOT_SUPPORT.toException(request.getRequestURI());
    }

    /**
     * 当出现多个符合匹配条件的ApiVersionCondition，优先匹配版本号较大的
     */
    @Override
    public int compareTo(ApiVersionCondition other, HttpServletRequest request) {
        return other.getApiVersion() - getApiVersion();
    }
}