package com.newland.tianyan.face.remote;


import com.newland.tianyan.face.remote.dto.milvus.*;
import com.newland.tianyan.face.remote.falback.AuthServiceFeignClientFallbackImpl;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import java.util.List;

/**
 * @author: RojiaHuang
 * @description:
 * @date: 2021/2/8
 */
@FeignClient(name = "vector-search-service", fallbackFactory = AuthServiceFeignClientFallbackImpl.class)
@RequestMapping("/backend/search/milvus")
public interface VectorSearchFeignService {

    @RequestMapping(value = "/insert", method = RequestMethod.POST)
    Long insert(@RequestBody InsertReq insertReq);

    @RequestMapping(value = "/query", method = RequestMethod.POST)
    List<QueryRes> query(@RequestBody QueryReq queryReq);

    @RequestMapping(value = "/delete", method = RequestMethod.POST)
    void delete(@RequestBody DeleteReq deleteReq);

    @RequestMapping(value = "/createCol", method = RequestMethod.POST)
    void createCollection(@RequestBody CreateColReq createColReq);

    @RequestMapping(value = "/dropCol", method = RequestMethod.POST)
    void dropCollection(@RequestBody DeleteColReq deleteReq);

    @RequestMapping(value = "/batchInsert", method = RequestMethod.POST)
    List<Long> batchInsert(@RequestBody BatchInsertReq batchInsertReq);

    @RequestMapping(value = "/batchQuery", method = RequestMethod.POST)
    List<List<QueryRes>> batchQuery(@RequestBody BatchQueryReq batchQueryReq);

    @RequestMapping(value = "/batchDelete", method = RequestMethod.POST)
    void batchDelete(@RequestBody BatchDeleteReq batchDeleteReq);
}
