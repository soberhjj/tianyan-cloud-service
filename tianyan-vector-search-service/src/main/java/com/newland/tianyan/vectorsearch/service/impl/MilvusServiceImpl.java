package com.newland.tianyan.vectorsearch.service.impl;

import com.google.gson.JsonObject;
import com.newland.tianya.commons.base.model.vectorsearch.QueryResDTO;
import com.newland.tianya.commons.base.utils.CosineDistanceTool;
import com.newland.tianyan.vectorsearch.service.IMilvusService;
import io.milvus.client.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * @Author: huangJunJie  2021-02-04 14:21
 */
@Service
@RefreshScope
public class MilvusServiceImpl implements IMilvusService {

    @Value("${milvus.host}")
    private String milvusServerHost;

    @Value("${milvus.port}")
    private Integer milvusServerPort;

    @Value("${milvus.nprobe}")
    private Integer nprobe;

    private ConnectParam getConnectParam() {
        return new ConnectParam.Builder().withHost(milvusServerHost).withPort(milvusServerPort).build();
    }

    @Override
    public void createCollection(String appId) {
        MilvusClient client = new MilvusGrpcClient(getConnectParam());
        CollectionMapping collectionMapping =
                new CollectionMapping.Builder(appId, 512)
                        .withIndexFileSize(1024)
                        .withMetricType(MetricType.L2)
                        .build();
        client.createCollection(collectionMapping);
        client.close();
    }

    @Override
    public void dropCollection(String appId) {
        MilvusClient client = new MilvusGrpcClient(getConnectParam());
        client.dropCollection(appId);
        client.close();
    }

    @Override
    public Long insert(String appId, List<Float> feature, Long entityId) {
        MilvusClient client = new MilvusGrpcClient(getConnectParam());
        List<List<Float>> features = new ArrayList<>();
        List<Long> entityIds = new ArrayList<>();
        features.add(feature);
        entityIds.add(entityId);

        InsertParam insertParam = new InsertParam.Builder(appId).withFloatVectors(features).withVectorIds(entityIds).build();
        InsertResponse inserRes = client.insert(insertParam);
        List<Long> insertEntityIds = inserRes.getVectorIds();

        client.close();

        return insertEntityIds.get(0);
    }

    @Override
    public List<QueryResDTO> query(String appId, List<Float> feature, Integer topK) {
        MilvusClient client = new MilvusGrpcClient(getConnectParam());

        List<List<Float>> features = new ArrayList<>();
        features.add(feature);

        JsonObject searchParamsJson = new JsonObject();
        searchParamsJson.addProperty("nprobe", nprobe);

        SearchParam searchParam = new SearchParam.Builder(appId)
                .withFloatVectors(features)
                .withTopK(topK)
                .withParamsInJson(searchParamsJson.toString())
                .build();

        SearchResponse searchResponse = client.search(searchParam);
        client.close();

        List<Long> ids = searchResponse.getResultIdsList().get(0);
        List<Float> distances = searchResponse.getResultDistancesList().get(0);

        List<Float> transDistances = new LinkedList<>();
        for (Float distance : distances) {
            transDistances.add(CosineDistanceTool.convertEur2CosUp(distance));
        }

        List<QueryResDTO> res = new LinkedList<>();
        for (int i = 0; i < ids.size(); i++) {
            QueryResDTO temp = new QueryResDTO();
            temp.setEntityId(ids.get(i));
            temp.setDistance(transDistances.get(i));
            res.add(temp);
        }

        return res;
    }

    @Override
    public void delete(String appId, Long entityId) {
        MilvusClient client = new MilvusGrpcClient(getConnectParam());

        List<Long> entityIds = new ArrayList<>();
        entityIds.add(entityId);

        client.deleteEntityByID(appId, entityIds);
        client.flush(appId);
        client.close();
    }

    @Override
    public List<Long> batchInsert(String appId, List<List<Float>> features, List<Long> entityIds) {
        MilvusClient client = new MilvusGrpcClient(getConnectParam());

        InsertParam insertParam = new InsertParam.Builder(appId).withFloatVectors(features).withVectorIds(entityIds).build();
        InsertResponse inserRes = client.insert(insertParam);
        client.close();

        List<Long> ids = inserRes.getVectorIds();
        return ids;
    }

    @Override
    public List<List<QueryResDTO>> batchQuery(String appId, List<List<Float>> features, Integer topK) {
        MilvusClient client = new MilvusGrpcClient(getConnectParam());

        JsonObject searchParamsJson = new JsonObject();
        searchParamsJson.addProperty("nprobe", nprobe);

        SearchParam searchParam = new SearchParam.Builder(appId)
                .withFloatVectors(features)
                .withTopK(topK)
                .withParamsInJson(searchParamsJson.toString())
                .build();

        SearchResponse searchResponse = client.search(searchParam);
        client.close();

        List<List<Long>> resultIdsList = searchResponse.getResultIdsList();
        List<List<Float>> resultDistancesList = searchResponse.getResultDistancesList();

        int batchSize = resultIdsList.size();
        List<List<QueryResDTO>> batchRes = new LinkedList<>();

        for (int i = 0; i < batchSize; i++) {
            List<Long> ids = resultIdsList.get(i);
            List<Float> distances = resultDistancesList.get(i);

            List<Float> transDistances = new LinkedList<>();
            for (Float distance : distances) {
                transDistances.add(CosineDistanceTool.convertEur2CosUp(distance));
            }

            List<QueryResDTO> res = new LinkedList<>();
            for (int j = 0; j < ids.size(); j++) {
                QueryResDTO temp = new QueryResDTO();
                temp.setEntityId(ids.get(j));
                temp.setDistance(transDistances.get(j));
                res.add(temp);
            }
            batchRes.add(res);
        }
        return batchRes;
    }

    @Override
    public void batchDelete(String appId, List<Long> entityIds) {
        MilvusClient client = new MilvusGrpcClient(getConnectParam());

        client.deleteEntityByID(appId, entityIds);
        client.flush(appId);
        client.close();
    }
}
