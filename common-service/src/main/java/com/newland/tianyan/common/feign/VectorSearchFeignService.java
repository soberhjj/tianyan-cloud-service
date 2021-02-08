package com.newland.tianyan.common.feign;

import com.newland.tianyan.common.feign.dto.milvus.*;
import com.newland.tianyan.common.feign.falback.AuthServiceFeignClientFallbackImpl;
import com.newland.tianyan.common.feign.falback.FeignConfiguration;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;

/**
 * @author: RojiaHuang
 * @description:
 * @date: 2021/2/8
 */
@FeignClient(name = "vector-search-service", fallbackFactory = AuthServiceFeignClientFallbackImpl.class,
        configuration = FeignConfiguration.class)
public interface VectorSearchFeignService {
    @PostMapping("/insert")
    Long insert(@RequestBody InsertReq insertReq);

    @PostMapping("/query")
    List<QueryRes> query(@RequestBody QueryReq queryReq);

    @PostMapping("/delete")
    void delete(@RequestBody DeleteReq deleteReq);

    @PostMapping("/createCol")
    void createCollection(@RequestBody CreateColReq createColReq);

    @PostMapping("/dropCol")
    void dropCollection(@RequestBody DeleteColReq deleteReq);

    @PostMapping("/batchInsert")
    List<Long> batchInsert(@RequestBody BatchInsertReq batchInsertReq);

    @PostMapping("/batchQuery")
    List<List<QueryRes>> batchQuery(@RequestBody BatchQueryReq batchQueryReq);

    @PostMapping("/batchDelete")
    void batchDelete(@RequestBody BatchDeleteReq batchDeleteReq);
}
