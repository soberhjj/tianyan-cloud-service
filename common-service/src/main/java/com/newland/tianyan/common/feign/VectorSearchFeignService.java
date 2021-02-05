package com.newland.tianyan.common.feign;

import com.newland.tianyan.common.feign.dto.MilvusQueryRes;
import com.newland.tianyan.common.feign.falback.FeignConfiguration;
import com.newland.tianyan.common.feign.falback.ImageServiceFeignClientFallbackImpl;
import org.springframework.cloud.openfeign.FeignClient;

import java.util.List;

/**
 * @author: RojiaHuang
 * @description:
 * @date: 2021/2/3
 */
@FeignClient(name = "vector-search-service", fallback = ImageServiceFeignClientFallbackImpl.class,
        configuration = FeignConfiguration.class)
public interface VectorSearchFeignService {

    List<MilvusQueryRes> query(String collectionId, List<Float> feature, List<Long> gids, Integer topK);

    Integer delete(String collectionId, Long id);

    Integer deleteBatch(String collectionId, List<Long> idList);


    Integer insert(String collectionId, List<Float> feature, Long entityId, Long gid, Long uid);

    List<Long> batchInsert(String collectionId, List<List<Float>> features, List<Long> entityIds, List<Long> gids, List<Long> uids);

    void createCollection(String collectionId);

    void dropCollection(String collectionId);
}
