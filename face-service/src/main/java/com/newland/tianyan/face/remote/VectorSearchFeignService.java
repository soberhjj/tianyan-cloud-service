package com.newland.tianyan.face.remote;


import com.newland.tianyan.common.model.vectorsearchservice.IMilvusApi;
import com.newland.tianyan.face.remote.fallback.AuthServiceFeignClientFallbackImpl;
import org.springframework.cloud.openfeign.FeignClient;

/**
 * @author: RojiaHuang
 * @description:
 * @date: 2021/2/8
 */
@FeignClient(name = "vector-search-service", fallbackFactory = AuthServiceFeignClientFallbackImpl.class)
public interface VectorSearchFeignService extends IMilvusApi {

}
