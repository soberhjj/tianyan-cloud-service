package com.newland.tianyan.face.remote;


import com.newland.tianyan.face.remote.fallback.AuthServiceFeignClientFallbackImpl;
import org.springframework.cloud.openfeign.FeignClient;
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
    Long insert( InsertReq insertReq);

    @RequestMapping(value = "/query", method = RequestMethod.POST)
    List<QueryRes> query(QueryReq queryReq);

    @RequestMapping(value = "/delete", method = RequestMethod.POST)
    void delete( DeleteReq deleteReq);

    @RequestMapping(value = "/createCol", method = RequestMethod.POST)
    void createCollection( CreateColReq createColReq);

    @RequestMapping(value = "/dropCol", method = RequestMethod.POST)
    void dropCollection( DeleteColReq deleteReq);

    @RequestMapping(value = "/batchInsert", method = RequestMethod.POST)
    List<Long> batchInsert( BatchInsertReq batchInsertReq);

    @RequestMapping(value = "/batchQuery", method = RequestMethod.POST)
    List<List<QueryRes>> batchQuery( BatchQueryReq batchQueryReq);

    @RequestMapping(value = "/batchDelete", method = RequestMethod.POST)
    void batchDelete( BatchDeleteReq batchDeleteReq);
}
