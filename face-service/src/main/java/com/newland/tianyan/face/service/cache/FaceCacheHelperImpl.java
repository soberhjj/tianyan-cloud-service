package com.newland.tianyan.face.service.cache;


import com.newland.tianyan.common.model.vectorSearchService.dto.*;
import com.newland.tianyan.common.utils.utils.FeaturesTool;
import com.newland.tianyan.face.entity.Face;
import com.newland.tianyan.face.exception.ApiReturnErrorCode;
import com.newland.tianyan.face.remote.VectorSearchFeignService;
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
public class FaceCacheHelperImpl<T> implements ICacheHelper<T> {
    @Autowired
    private VectorSearchFeignService milvusService;

    private String getCollectionName(Long appId) {
        return "FACE_" + appId;
    }

    public List<QueryResDTO> query(Long appId, List<Float> feature, List<Long> gids, Integer topK) {
        QueryReqDTO queryReq = QueryReqDTO.builder()
                .appId(getCollectionName(appId))
                .feature(feature)
                .topK(topK)
                .build();
        return milvusService.query(queryReq);
    }

    /**
     * 分装faceId值DeleteFaceRequest
     */
    @Override
    public Integer delete(Long collectionId, Long id) {

        int result = 1;
        try {
            DeleteReqDTO deleteReq = DeleteReqDTO.builder()
                    .appId(getCollectionName(collectionId))
                    .entityId(id)
                    .build();
            milvusService.delete(deleteReq);
        } catch (RuntimeException exception) {
            exception.printStackTrace();
            result = -1;
        }
        return result;
    }

    /**
     * 分装faceId值DeleteFaceRequest
     */
    @Override
    public Integer deleteBatch(Long collectionId, List<Long> idList) {

        int result = 1;
        try {
            BatchDeleteReqDTO batchDeleteReq = BatchDeleteReqDTO.builder()
                    .appId(getCollectionName(collectionId))
                    .entityIds(idList)
                    .build();
            milvusService.batchDelete(batchDeleteReq);
        } catch (RuntimeException exception) {
            exception.printStackTrace();
            result = -1;
        }
        return result;
    }

    @Override
    public Long add(T entity) {

        Face dto = (Face) entity;
        List<Float> feature = this.convertByteArrayToList(dto);
        InsertReqDTO insertReq = InsertReqDTO.builder()
                .appId(getCollectionName(dto.getAppId()))
                .entityId(dto.getId())
                .feature(feature)
                .build();
        return milvusService.insert(insertReq);
    }

    @Override
    public List<Long> addBatch(List<T> entityList) {

        if (CollectionUtils.isEmpty(entityList)) {
            throw new IllegalArgumentException("mainDataList must not be empty");
        }
        int size = entityList.size();
        List<Face> insertSourceList = new ArrayList<>();
        entityList.forEach(mainDataItem -> insertSourceList.add((Face) mainDataItem));

        Long appId = insertSourceList.get(0).getAppId();
        List<List<Float>> features = new ArrayList<>(size);
        List<Long> entityIds = new ArrayList<>(size);
        List<Long> gids = new ArrayList<>(size);
        List<Long> uids = new ArrayList<>(size);
        insertSourceList.forEach(face -> {
            features.add(this.convertByteArrayToList(face));
            entityIds.add(face.getId());
            gids.add(face.getGid());
            uids.add(face.getUid());
        });

        BatchInsertReqDTO batchInsertReq = BatchInsertReqDTO.builder()
                .appId(getCollectionName(appId))
                .entityIds(entityIds)
                .features(features)
                .build();
        return milvusService.batchInsert(batchInsertReq);
    }

    public List<Float> convertByteArrayToList(Face entity) {
        if (entity == null || entity.getFeatures() == null) {
            throw ApiReturnErrorCode.ILLEGAL_ARGUMENT.toException("feature must not be null");
        }

        return FeaturesTool.convertByteArrayToList(entity.getFeatures());
    }

    @Override
    public Integer createCollection(Long collectionId) {
        int result = 1;
        try {
            CreateColReqDTO createColReq = CreateColReqDTO.builder()
                    .appId(getCollectionName(collectionId))
                    .build();
            milvusService.createCollection(createColReq);
        } catch (RuntimeException exception) {
            exception.printStackTrace();
            result = -1;
        }
        return result;
    }

    @Override
    public Integer deleteCollection(Long collectionId) {
        int result = 1;
        try {
            DeleteColReqDTO deleteColReq = DeleteColReqDTO.builder()
                    .appId(getCollectionName(collectionId))
                    .build();
            milvusService.dropCollection(deleteColReq);
        } catch (RuntimeException exception) {
            exception.printStackTrace();
            result = -1;
        }
        return result;
    }
}
