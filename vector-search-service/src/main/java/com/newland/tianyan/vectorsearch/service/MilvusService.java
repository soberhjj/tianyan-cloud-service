package com.newland.tianyan.vectorsearch.service;

import com.google.gson.JsonObject;
import com.newland.tianyan.vectorsearch.vo.QueryRes;
import com.newland.tianyan.vectorsearch.utils.CosineDistanceTool;
import io.milvus.client.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

/**
 * @Author: huangJunJie  2021-02-04 14:21
 */
@Service
@RefreshScope
public class MilvusService {

    @Value("${milvus.host}")
    private String MILVUS_SERVER_HOST;

    @Value("${milvus.port}")
    private Integer MILVUS_SERVER_PORT;

    @Value("${milvus.nprobe}")
    private Integer NPROBE;

    private ConnectParam getConnectParam() {
        return new ConnectParam.Builder().withHost(MILVUS_SERVER_HOST).withPort(MILVUS_SERVER_PORT).build();
    }

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

    public void dropCollection(String appId) {
        MilvusClient client = new MilvusGrpcClient(getConnectParam());
        client.dropCollection(appId);
        client.close();
    }

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

    public List<QueryRes> query(String appId, List<Float> feature, Integer topK) {
        MilvusClient client = new MilvusGrpcClient(getConnectParam());

        List<List<Float>> features = new ArrayList<>();
        features.add(feature);

        JsonObject searchParamsJson = new JsonObject();
        searchParamsJson.addProperty("nprobe", NPROBE);

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

        List<QueryRes> res = new LinkedList<>();
        for (int i = 0; i < ids.size(); i++) {
            QueryRes temp = new QueryRes();
            temp.setEntityId(ids.get(i));
            temp.setDistance(transDistances.get(i));
            res.add(temp);
        }

        return res;
    }

    public void delete(String appId, Long entityId) {
        MilvusClient client = new MilvusGrpcClient(getConnectParam());

        List<Long> entityIds = new ArrayList<>();
        entityIds.add(entityId);

        client.deleteEntityByID(appId, entityIds);
        client.flush(appId);
        client.close();
    }

    public List<Long> batchInsert(String appId, List<List<Float>> features, List<Long> entityIds) {
        MilvusClient client = new MilvusGrpcClient(getConnectParam());

        InsertParam insertParam = new InsertParam.Builder(appId).withFloatVectors(features).withVectorIds(entityIds).build();
        InsertResponse inserRes = client.insert(insertParam);
        client.close();

        List<Long> ids = inserRes.getVectorIds();
        return ids;
    }

    public List<List<QueryRes>> batchQuery(String appId, List<List<Float>> features, Integer topK) {
        MilvusClient client = new MilvusGrpcClient(getConnectParam());

        JsonObject searchParamsJson = new JsonObject();
        searchParamsJson.addProperty("nprobe", NPROBE);

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
        List<List<QueryRes>> batchRes = new LinkedList<>();

        for (int i = 0; i < batchSize; i++) {
            List<Long> ids = resultIdsList.get(i);
            List<Float> distances = resultDistancesList.get(i);

            List<Float> transDistances = new LinkedList<>();
            for (Float distance : distances) {
                transDistances.add(CosineDistanceTool.convertEur2CosUp(distance));
            }

            List<QueryRes> res = new LinkedList<>();
            for (int j = 0; j < ids.size(); j++) {
                QueryRes temp = new QueryRes();
                temp.setEntityId(ids.get(j));
                temp.setDistance(transDistances.get(j));
                res.add(temp);
            }
            batchRes.add(res);
        }
        return batchRes;
    }

    public void batchDelete(String appId, List<Long> entityIds) {
        MilvusClient client = new MilvusGrpcClient(getConnectParam());

        client.deleteEntityByID(appId, entityIds);
        client.flush(appId);
        client.close();
    }
}
