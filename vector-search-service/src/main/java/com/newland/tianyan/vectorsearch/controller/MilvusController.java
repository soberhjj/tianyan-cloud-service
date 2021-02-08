package com.newland.tianyan.vectorsearch.controller;

import com.newland.tianyan.vectorsearch.entity.*;
import com.newland.tianyan.vectorsearch.service.MilvusService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * @Author: huangJunJie  2021-02-04 14:59
 */
@RestController
@RequestMapping("/backend/search/milvus")
public class MilvusController {
    @Autowired
    MilvusService milvusService;

    @PostMapping("/insert")
    public Long insert(@RequestBody InsertReq insertReq) {
        return milvusService.insert(insertReq.getAppId(), insertReq.getFeature(), insertReq.getEntityId());
    }

    @PostMapping("/query")
    public List<QueryRes> query(@RequestBody QueryReq queryReq) {
        return milvusService.query(queryReq.getAppId(), queryReq.getFeature(), queryReq.getTopK());
    }

    @PostMapping("/delete")
    public void delete(@RequestBody DeleteReq deleteReq) {
        milvusService.delete(deleteReq.getAppId(),deleteReq.getEntityId());
    }

    @PostMapping("/createCol")
    public void createCollection(@RequestBody CreateColReq createColReq) {
        milvusService.createCollection(createColReq.getAppId());
    }

    @PostMapping("/dropCol")
    public void dropCollection(@RequestBody DeleteColReq deleteReq) {
        milvusService.dropCollection(deleteReq.getAppId());
    }

    @PostMapping("/batchInsert")
    public List<Long> batchInsert(@RequestBody BatchInsertReq batchInsertReq) {
        return milvusService.batchInsert(batchInsertReq.getAppId(),batchInsertReq.getFeatures(),batchInsertReq.getEntityIds());
    }

    @PostMapping("/batchQuery")
    public List<List<QueryRes>> batchQuery(@RequestBody BatchQueryReq batchQueryReq) {
        return milvusService.batchQuery(batchQueryReq.getAppId(),batchQueryReq.getFeatures(),batchQueryReq.getTopK());
    }

    @PostMapping("/batchDelete")
    public void batchDelete(@RequestBody BatchDeleteReq batchDeleteReq) {
       milvusService.batchDelete(batchDeleteReq.getAppId(),batchDeleteReq.getEntityIds());
    }
}
