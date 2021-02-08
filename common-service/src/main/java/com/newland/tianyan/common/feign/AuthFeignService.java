package com.newland.tianyan.common.feign;


import com.newland.tianyan.common.feign.dto.ClientRequest;
import com.newland.tianyan.common.feign.falback.FeignConfiguration;
import com.newland.tianyan.common.feign.falback.ImageServiceFeignClientFallbackImpl;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

@FeignClient(name = "auth-service", fallback = ImageServiceFeignClientFallbackImpl.class,
        configuration = FeignConfiguration.class)
public interface AuthFeignService {
    @RequestMapping(value = "/addClient", method = RequestMethod.POST)
    void addClient(@RequestBody ClientRequest request);

    @RequestMapping(value = "/deleteClient", method = RequestMethod.POST)
    void deleteClient(@RequestBody ClientRequest request);
}
