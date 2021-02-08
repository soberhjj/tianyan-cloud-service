package com.newland.tianyan.face.cache;


import com.newland.tianyan.common.feign.VectorSearchFeignService;
import com.newland.tianyan.common.feign.dto.milvus.*;
import com.newland.tianyan.face.common.utils.FeaturesTool;
import com.newland.tianyan.face.domain.FaceInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
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
@Component
public class FaceCacheHelperImpl<T> implements ICacheHelper<T> {
    @Autowired
    private VectorSearchFeignService vectorSearchFeignService;

    private String getCollectionName(Long appId) {
        return "FACE_" + appId;
    }

    public List<QueryRes> query(Long appId, List<Float> feature, List<Long> gids, Integer topK) {
        QueryReq queryReq = QueryReq.builder()
                .appId(getCollectionName(appId))
                .feature(feature)
                .topK(topK)
                .build();
        return vectorSearchFeignService.query(queryReq);
    }

    /**
     * 分装faceId值DeleteFaceRequest
     */
    @Override
    public Integer delete(Long collectionId, Long id) {

        int result = 1;
        try {
            DeleteReq deleteReq = DeleteReq.builder()
                    .appId(getCollectionName(collectionId))
                    .entityId(id)
                    .build();
            vectorSearchFeignService.delete(deleteReq);
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
            BatchDeleteReq batchDeleteReq = BatchDeleteReq.builder()
                    .appId(getCollectionName(collectionId))
                    .entityIds(idList)
                    .build();
            vectorSearchFeignService.batchDelete(batchDeleteReq);
        } catch (RuntimeException exception) {
            exception.printStackTrace();
            result = -1;
        }
        return result;
    }

    @Override
    public Long add(T entity) {

        FaceInfo dto = (FaceInfo) entity;
        List<Float> feature = this.convertByteArrayToList(dto);
        InsertReq insertReq = InsertReq.builder()
                .appId(getCollectionName(dto.getAppId()))
                .feature(feature)
                .entityId(dto.getId())
                .build();
        return vectorSearchFeignService.insert(insertReq);
    }

    @Override
    public List<Long> addBatch(List<T> entityList) {

        if (CollectionUtils.isEmpty(entityList)) {
            throw new IllegalArgumentException("mainDataList must not be empty");
        }
        int size = entityList.size();
        List<FaceInfo> insertSourceList = new ArrayList<>();
        entityList.forEach(mainDataItem -> insertSourceList.add((FaceInfo) mainDataItem));

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

        BatchInsertReq batchInsertReq = BatchInsertReq.builder()
                .appId(getCollectionName(appId))
                .entityIds(entityIds)
                .features(features)
                .build();
        return vectorSearchFeignService.batchInsert(batchInsertReq);
    }

    public List<Float> convertByteArrayToList(FaceInfo entity) {


        return FeaturesTool.convertByteArrayToList(entity.getFeatures());
    }

    @Override
    public Integer createCollection(Long collectionId) {
        int result = 1;
        try {
            CreateColReq createColReq = CreateColReq.builder()
                    .appId(getCollectionName(collectionId))
                    .build();
            vectorSearchFeignService.createCollection(createColReq);
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
            DeleteColReq deleteColReq = DeleteColReq.builder()
                    .appId(getCollectionName(collectionId))
                    .build();
            vectorSearchFeignService.dropCollection(deleteColReq);
        } catch (RuntimeException exception) {
            exception.printStackTrace();
            result = -1;
        }
        return result;
    }
}
