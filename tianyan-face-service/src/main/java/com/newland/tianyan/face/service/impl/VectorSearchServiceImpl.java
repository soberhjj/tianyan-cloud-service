package com.newland.tianyan.face.service.impl;


import com.newland.tianya.commons.base.exception.BaseException;
import com.newland.tianya.commons.base.model.vectorsearch.*;
import com.newland.tianya.commons.base.utils.FeaturesTool;
import com.newland.tianyan.face.domain.entity.FaceDO;
import com.newland.tianyan.face.domain.vo.FaceSearchVo;
import com.newland.tianyan.face.feign.client.VectorSearchFeignService;
import com.newland.tianyan.face.service.IVectorSearchService;
import com.newland.tianyan.face.utils.VectorSearchKeyUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author: RojiaHuang
 * @description: 人脸缓存接入Milvus参数封装方法
 * @date: 2021/1/12
 */
@Service
@Slf4j
public class VectorSearchServiceImpl<T> implements IVectorSearchService<T> {
    @Value("${face-search-defaultTopK}")
    private Integer defaultTopK;

    @Autowired
    private VectorSearchFeignService vectorSearchService;

    private String getCollectionName(Long appId) {
        return "FACE_" + appId;
    }

    @Override
    public List<FaceSearchVo> query(Long appId, List<Float> feature) throws BaseException {
        QueryReqDTO queryReq = QueryReqDTO.builder()
                .appId(getCollectionName(appId))
                .feature(feature)
                .topK(defaultTopK)
                .build();
        List<QueryResDTO> queryResult = vectorSearchService.query(queryReq);

        List<FaceSearchVo> convertResultList = new ArrayList<>(queryResult.size());
        if (CollectionUtils.isEmpty(queryResult)) {
            return convertResultList;
        }
        for (QueryResDTO vectorsQueryRes : queryResult) {
            Long vectorId = vectorsQueryRes.getEntityId();
            Long gid = VectorSearchKeyUtils.splitGid(vectorId);
            Long uid = VectorSearchKeyUtils.splitUid(vectorId);
            FaceSearchVo faceSearchVo = FaceSearchVo.builder()
                    .vectorId(vectorsQueryRes.getEntityId())
                    .distance(String.valueOf(vectorsQueryRes.getDistance()))
                    .gid(gid)
                    .uid(uid)
                    .build();
            convertResultList.add(faceSearchVo);
        }
        return convertResultList;
    }

    @Override
    public List<FaceSearchVo> filterSameGroupSameUser(List<FaceSearchVo> source) {
        List<Long> allVectorSearchKeys = source.stream().map(FaceSearchVo::getVectorId).collect(Collectors.toList());
        List<Long> filterVectorSearchKeys = VectorSearchKeyUtils.filterSameGroupSameUser(allVectorSearchKeys);
        List<FaceSearchVo> target = new ArrayList<>();
        for (FaceSearchVo item : source) {
            if (filterVectorSearchKeys.contains(item.getVectorId())) {
                target.add(item);
            }
        }
        return target;
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
