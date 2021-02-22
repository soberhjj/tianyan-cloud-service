package com.newland.tianyan.face.remote;


import com.newland.tianyan.common.model.vectorSearchService.IMilvusApi;
import com.newland.tianyan.face.remote.fallback.AuthServiceFeignClientFallbackImpl;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * @author: RojiaHuang
 * @description:
 * @date: 2021/2/8
 */
@FeignClient(name = "vector-search-service", fallbackFactory = AuthServiceFeignClientFallbackImpl.class)
public interface VectorSearchFeignService extends IMilvusApi {

}
