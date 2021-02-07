package com.newland.tianyan.common.feign.falback;


import com.newland.tianyan.common.feign.AuthFeignService;
import com.newland.tianyan.common.feign.dto.ClientRequest;
import org.springframework.stereotype.Component;

/**
 * @author: RojiaHuang
 * @description: 降级处理
 * @date: 2021/2/2
 */
@Component
public class AuthServiceFeignClientFallbackImpl implements AuthFeignService {

    @Override
    public void addClient(ClientRequest request) {

    }

    @Override
    public void deleteClient(ClientRequest request) {

    }
}
