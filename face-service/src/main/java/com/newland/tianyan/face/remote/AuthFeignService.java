package com.newland.tianyan.face.remote;



import com.newland.tianyan.face.remote.dto.auth.AddClientRequest;
import com.newland.tianyan.face.remote.falback.AuthServiceFeignClientFallbackImpl;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

@FeignClient(name = "auth-service", fallbackFactory = AuthServiceFeignClientFallbackImpl.class)
public interface AuthFeignService {
    @RequestMapping(value = "/addClient", method = RequestMethod.POST)
    void addClient(@RequestBody AddClientRequest request);

    @RequestMapping(value = "/deleteClient", method = RequestMethod.POST)
    void deleteClient(@RequestBody AddClientRequest request);
}
