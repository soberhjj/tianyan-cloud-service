package com.newland.tianyan.face.service.impl;


import com.newland.tianya.commons.base.exception.BaseException;
import com.newland.tianya.commons.base.model.vectorsearch.*;
import com.newland.tianya.commons.base.utils.FeaturesTool;
import com.newland.tianyan.face.domain.entity.FaceDO;
import com.newland.tianyan.face.feign.client.VectorSearchFeignService;
import com.newland.tianyan.face.service.IVectorSearchService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * @author: RojiaHuang
 * @description: 人脸缓存接入Milvus参数封装方法
 * @date: 2021/1/12
 */
@Service
@Slf4j
public class VectorSearchServiceImpl<T> implements IVectorSearchService<T> {
    @Autowired
    private VectorSearchFeignService vectorSearchService;

    private String getCollectionName(Long appId) {
        return "FACE_" + appId;
    }

    public List<QueryResDTO> query(Long appId, List<Float> feature, Integer topK) throws BaseException {
        QueryReqDTO queryReq = QueryReqDTO.builder()
                .appId(getCollectionName(appId))
                .feature(feature)
                .topK(topK)
                .build();
        return vectorSearchService.query(queryReq);
    }

    /**
     * 分装faceId值DeleteFaceRequest
     */
    @Override
    public void delete(Long collectionId, Long id) throws BaseException {

        DeleteReqDTO deleteReq = DeleteReqDTO.builder()
                .appId(getCollectionName(collectionId))
                .entityId(id)
                .build();
        vectorSearchService.delete(deleteReq);
    }

    /**
     * 分装faceId值DeleteFaceRequest
     */
    @Override
    public void deleteBatch(Long collectionId, List<Long> idList) throws BaseException {

        BatchDeleteReqDTO batchDeleteReq = BatchDeleteReqDTO.builder()
                .appId(getCollectionName(collectionId))
                .entityIds(idList)
                .build();
        vectorSearchService.batchDelete(batchDeleteReq);
    }

    @Override
    public Long add(T entity) throws BaseException {

        FaceDO dto = (FaceDO) entity;
        List<Float> feature = this.convertByteArrayToList(dto);
        InsertReqDTO insertReq = InsertReqDTO.builder()
                .appId(getCollectionName(dto.getAppId()))
                .entityId(dto.getId())
                .feature(feature)
                .build();
        return vectorSearchService.insert(insertReq);
    }

    @Override
    public List<Long> addBatch(List<T> entityList) throws BaseException {

        if (CollectionUtils.isEmpty(entityList)) {
            throw new IllegalArgumentException("mainDataList must not be empty");
        }
        int size = entityList.size();
        List<FaceDO> insertSourceList = new ArrayList<>();
        entityList.forEach(mainDataItem -> insertSourceList.add((FaceDO) mainDataItem));

        Long appId = insertSourceList.get(0).getAppId();
        List<List<Float>> features = new ArrayList<>(size);
        List<Long> entityIds = new ArrayList<>(size);
        insertSourceList.forEach(face -> {
            features.add(this.convertByteArrayToList(face));
            entityIds.add(face.getId());
        });

        BatchInsertReqDTO batchInsertReq = BatchInsertReqDTO.builder()
                .appId(getCollectionName(appId))
                .entityIds(entityIds)
                .features(features)
                .build();
        return vectorSearchService.batchInsert(batchInsertReq);
    }

    public List<Float> convertByteArrayToList(FaceDO entity) throws BaseException {
        return FeaturesTool.convertByteArrayToList(entity.getFeatures());
    }

    @Override
    public void createCollection(Long collectionId) throws BaseException {
        CreateColReqDTO createColReq = CreateColReqDTO.builder()
                .appId(getCollectionName(collectionId))
                .build();
        vectorSearchService.createCollection(createColReq);
    }

    @Override
    public void deleteCollection(Long collectionId) throws BaseException {
        DeleteColReqDTO deleteColReq = DeleteColReqDTO.builder()
                .appId(getCollectionName(collectionId))
                .build();
        vectorSearchService.dropCollection(deleteColReq);
    }
}
