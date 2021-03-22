package com.newland.tianyan.commons.webcore.annotation;

import java.lang.annotation.*;

/**
 * @author: https://mp.weixin.qq.com/s/m2HnUBXagKaLQjzww1s77g
 * @description:
 * @date: 2021/2/22
 */
@Target({ElementType.METHOD, ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface ApiVersion {
    /**
     * 标识版本号，从1开始
     */
    int value() default 1;
}

