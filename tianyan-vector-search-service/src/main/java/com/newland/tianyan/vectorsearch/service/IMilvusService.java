package com.newland.tianyan.vectorsearch.service;


import com.newland.tianya.commons.base.model.vectorsearch.QueryResDTO;

import java.util.List;

/**
 * @author: RojiaHuang
 * @description:
 * @date: 2021/2/23
 */
public interface IMilvusService {

    void createCollection(String appId);

    void dropCollection(String appId);

    Long insert(String appId, List<Float> feature, Long entityId);

    List<QueryResDTO> query(String appId, List<Float> feature, Integer topK);

    void delete(String appId, Long entityId);

    List<Long> batchInsert(String appId, List<List<Float>> features, List<Long> entityIds);

    List<List<QueryResDTO>> batchQuery(String appId, List<List<Float>> features, Integer topK);

    void batchDelete(String appId, List<Long> entityIds);
}
