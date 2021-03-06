package com.newland.tianyan.common.api;

import com.newland.tianyan.common.model.vectorsearch.*;
import com.newland.tianyan.common.version.ApiVersion;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.validation.Valid;
import java.util.List;

/**
 * @author: RojiaHuang
 * @description:
 * @date: 2021/2/20
 */
@RequestMapping("/vectorsearch/v1")
public interface IVectorSearchApi {
    @PostMapping("/insert")
    @ApiVersion(1)
    Long insert(@RequestBody @Valid InsertReqDTO insertReqDTO);

    @PostMapping("/query")
    @ApiVersion(1)
    List<QueryResDTO> query(@RequestBody @Valid QueryReqDTO queryReqDTO);

    @PostMapping("/delete")
    @ApiVersion(1)
    void delete(@RequestBody @Valid DeleteReqDTO deleteReqDTO);

    @PostMapping("/createCol")
    @ApiVersion(1)
    void createCollection(@RequestBody @Valid CreateColReqDTO createColReqDTO);

    @PostMapping("/dropCol")
    @ApiVersion(1)
    void dropCollection(@RequestBody @Valid DeleteColReqDTO deleteReq);

    @PostMapping("/batchInsert")
    @ApiVersion(1)
    List<Long> batchInsert(@RequestBody @Valid BatchInsertReqDTO batchInsertReq);

    @PostMapping("/batchQuery")
    @ApiVersion(1)
    List<List<QueryResDTO>> batchQuery(@RequestBody @Valid BatchQueryReqDTO batchQueryReqDTO);

    @PostMapping("/batchDelete")
    @ApiVersion(1)
    void batchDelete(@RequestBody @Valid BatchDeleteReqDTO batchDeleteReq);
}
