package com.newland.tianyan.vectorsearch.controller;


import com.newland.tianya.commons.base.model.vectorsearch.*;
import com.newland.tianyan.commons.webcore.api.IVectorSearchApi;
import com.newland.tianyan.vectorsearch.service.IMilvusService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.util.List;

/**
 * @Author: huangJunJie  2021-02-04 14:59
 */
@RestController
public class VectorSearchController implements IVectorSearchApi {
    @Autowired
    private IMilvusService milvusService;

    @Override
    @PostMapping("/insert")
    public Long insert(@RequestBody @Valid InsertReqDTO insertReq) {
        return milvusService.insert(insertReq.getAppId(), insertReq.getFeature(), insertReq.getEntityId());
    }

    @Override
    @PostMapping("/query")
    public List<QueryResDTO> query(@RequestBody @Valid QueryReqDTO queryReq) {
        return milvusService.query(queryReq.getAppId(), queryReq.getFeature(), queryReq.getTopK());
    }

    @Override
    @PostMapping("/delete")
    public void delete(@RequestBody @Valid DeleteReqDTO deleteReq) {
        milvusService.delete(deleteReq.getAppId(), deleteReq.getEntityId());
    }

    @Override
    @PostMapping("/createCol")
    public void createCollection(@RequestBody @Valid CreateColReqDTO createColReq) {
        milvusService.createCollection(createColReq.getAppId());
    }

    @Override
    @PostMapping("/dropCol")
    public void dropCollection(@RequestBody @Valid DeleteColReqDTO deleteReq) {
        milvusService.dropCollection(deleteReq.getAppId());
    }

    @Override
    @PostMapping("/batchInsert")
    public List<Long> batchInsert(@RequestBody @Valid BatchInsertReqDTO batchInsertReq) {
        return milvusService.batchInsert(batchInsertReq.getAppId(), batchInsertReq.getFeatures(), batchInsertReq.getEntityIds());
    }

    @Override
    @PostMapping("/batchQuery")
    public List<List<QueryResDTO>> batchQuery(@RequestBody @Valid BatchQueryReqDTO batchQueryReq) {
        return milvusService.batchQuery(batchQueryReq.getAppId(), batchQueryReq.getFeatures(), batchQueryReq.getTopK());
    }

    @Override
    @PostMapping("/batchDelete")
    public void batchDelete(@RequestBody @Valid BatchDeleteReqDTO batchDeleteReq) {
        milvusService.batchDelete(batchDeleteReq.getAppId(), batchDeleteReq.getEntityIds());
    }
}
