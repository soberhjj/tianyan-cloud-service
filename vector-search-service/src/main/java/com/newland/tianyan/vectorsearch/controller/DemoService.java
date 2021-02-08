package com.newland.tianyan.vectorsearch.controller;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.stereotype.Service;

/**
 * @author: RojiaHuang
 * @description:
 * @date: 2021/2/4
 */
@RefreshScope
@Service
public class DemoService {

    @Value("${milvus.host}")
    private String userId;

    public String  test(){
        return userId;
    }
}
