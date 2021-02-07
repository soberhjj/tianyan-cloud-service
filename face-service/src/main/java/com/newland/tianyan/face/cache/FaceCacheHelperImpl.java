package com.newland.tianyan.face.cache;


import com.newland.tianyan.common.feign.VectorSearchFeignService;
import com.newland.tianyan.common.feign.dto.MilvusQueryRes;
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

    public List<MilvusQueryRes> query(Long appId, List<Float> feature, List<Long> gids, Integer topK) {
        return vectorSearchFeignService.query(getCollectionName(appId), feature, gids, topK);
    }

    /**
     * 分装faceId值DeleteFaceRequest
     */
    @Override
    public Integer delete(Long collectionId, Long id) {

        int result = 1;
        try {
            vectorSearchFeignService.delete(getCollectionName(collectionId), id);
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
            vectorSearchFeignService.deleteBatch(getCollectionName(collectionId), idList);
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
        Integer result = vectorSearchFeignService.insert(getCollectionName(dto.getAppId()), feature, dto.getId(), dto.getGid(), dto.getUid());
        return Integer.toUnsignedLong(result);
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
        return vectorSearchFeignService.batchInsert(getCollectionName(appId), features, entityIds, gids, uids);
    }

    public List<Float> convertByteArrayToList(FaceInfo entity) {


        return FeaturesTool.convertByteArrayToList(entity.getFeatures());
    }

    @Override
    public Integer createCollection(Long collectionId) {
        int result = 1;
        try {
            vectorSearchFeignService.createCollection(getCollectionName(collectionId));
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
            vectorSearchFeignService.dropCollection(getCollectionName(collectionId));
        } catch (RuntimeException exception) {
            exception.printStackTrace();
            result = -1;
        }
        return result;
    }
}
