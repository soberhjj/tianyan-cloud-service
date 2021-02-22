package com.newland.tianyan.common.api;

import com.newland.tianyan.common.model.vectorsearchservice.*;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

/**
 * @author: RojiaHuang
 * @description:
 * @date: 2021/2/20
 */
@RequestMapping("/vectorsearch/v1")
public interface IVectorSearchApi {
    @PostMapping("/insert")
    Long insert(@RequestBody InsertReqDTO insertReqDTO);

    @PostMapping("/query")
    List<QueryResDTO> query(@RequestBody QueryReqDTO queryReqDTO);

    @PostMapping("/delete")
    void delete(@RequestBody DeleteReqDTO deleteReqDTO);

    @PostMapping("/createCol")
    void createCollection(@RequestBody CreateColReqDTO createColReqDTO);

    @PostMapping("/dropCol")
    void dropCollection(@RequestBody DeleteColReqDTO deleteReq);

    @PostMapping("/batchInsert")
    List<Long> batchInsert(@RequestBody BatchInsertReqDTO batchInsertReq);

    @PostMapping("/batchQuery")
    List<List<QueryResDTO>> batchQuery(@RequestBody BatchQueryReqDTO batchQueryReqDTO);

    @PostMapping("/batchDelete")
    void batchDelete(@RequestBody BatchDeleteReqDTO batchDeleteReq);
}
