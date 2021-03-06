package com.newland.tianyan.face.feign.client;


import com.newland.tianyan.common.api.IVectorSearchApi;
import com.newland.tianyan.face.feign.fallback.AuthServiceFeignClientFallbackImpl;
import org.springframework.cloud.openfeign.FeignClient;

/**
 * @author: RojiaHuang
 * @description:
 * @date: 2021/2/8
 */
@FeignClient(name = "vector-search-service", fallbackFactory = AuthServiceFeignClientFallbackImpl.class)
public interface VectorSearchFeignService extends IVectorSearchApi {

}
