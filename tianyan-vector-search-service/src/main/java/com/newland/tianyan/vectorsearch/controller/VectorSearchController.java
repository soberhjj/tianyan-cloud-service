package com.newland.tianyan.vectorsearch.controller;

import com.newland.tianyan.common.model.vectorsearchservice.*;
import com.newland.tianyan.common.api.IVectorSearchApi;
import com.newland.tianyan.vectorsearch.service.MilvusService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


import java.util.List;

/**
 * @Author: huangJunJie  2021-02-04 14:59
 */
@RestController
@RequestMapping("/backend/search/milvus")
public class VectorSearchController implements IVectorSearchApi {
    @Autowired
    private MilvusService milvusService;

    @Override
    @PostMapping("/insert")
    public Long insert(@RequestBody InsertReqDTO insertReq) {
        return milvusService.insert(insertReq.getAppId(), insertReq.getFeature(), insertReq.getEntityId());
    }

    @Override
    @PostMapping("/query")
    public List<QueryResDTO> query(@RequestBody QueryReqDTO queryReq) {
        return milvusService.query(queryReq.getAppId(), queryReq.getFeature(), queryReq.getTopK());
    }

    @Override
    @PostMapping("/delete")
    public void delete(@RequestBody DeleteReqDTO deleteReq) {
        milvusService.delete(deleteReq.getAppId(), deleteReq.getEntityId());
    }

    @Override
    @PostMapping("/createCol")
    public void createCollection(@RequestBody CreateColReqDTO createColReq) {
        milvusService.createCollection(createColReq.getAppId());
    }

    @Override
    @PostMapping("/dropCol")
    public void dropCollection(@RequestBody DeleteColReqDTO deleteReq) {
        milvusService.dropCollection(deleteReq.getAppId());
    }

    @Override
    @PostMapping("/batchInsert")
    public List<Long> batchInsert(@RequestBody BatchInsertReqDTO batchInsertReq) {
        return milvusService.batchInsert(batchInsertReq.getAppId(), batchInsertReq.getFeatures(), batchInsertReq.getEntityIds());
    }

    @Override
    @PostMapping("/batchQuery")
    public List<List<QueryResDTO>> batchQuery(@RequestBody BatchQueryReqDTO batchQueryReq) {
        return milvusService.batchQuery(batchQueryReq.getAppId(), batchQueryReq.getFeatures(), batchQueryReq.getTopK());
    }

    @Override
    @PostMapping("/batchDelete")
    public void batchDelete(@RequestBody BatchDeleteReqDTO batchDeleteReq) {
        milvusService.batchDelete(batchDeleteReq.getAppId(), batchDeleteReq.getEntityIds());
    }
}
