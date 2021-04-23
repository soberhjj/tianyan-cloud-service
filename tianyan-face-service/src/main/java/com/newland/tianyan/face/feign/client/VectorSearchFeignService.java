package com.newland.tianyan.face.feign.client;


import com.newland.tianyan.commons.webcore.api.IVectorSearchApi;
import com.newland.tianyan.face.feign.FeignConfiguration;
import com.newland.tianyan.face.feign.FeignConfiguration2;
import org.springframework.cloud.openfeign.FeignClient;

/**
 * @author: RojiaHuang
 * @description:
 * @date: 2021/2/8
 */
@FeignClient(name = "vector-search-service",
        configuration = {FeignConfiguration.class, FeignConfiguration2.class})
public interface VectorSearchFeignService extends IVectorSearchApi {

}
