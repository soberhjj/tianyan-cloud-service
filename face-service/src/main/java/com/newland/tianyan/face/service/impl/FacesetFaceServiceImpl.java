package com.newland.tianyan.face.service.impl;

import com.googlecode.protobuf.format.JsonFormat;
import com.newland.tianyan.common.feign.ImageStoreFeignService;
import com.newland.tianyan.common.feign.dto.image.UploadReq;
import com.newland.tianyan.common.feign.dto.milvus.QueryRes;
import com.newland.tianyan.common.utils.exception.CommonException;
import com.newland.tianyan.common.utils.message.NLBackend;
import com.newland.tianyan.common.utils.utils.LogUtils;
import com.newland.tianyan.common.utils.utils.ProtobufUtils;
import com.newland.tianyan.face.cache.FaceCacheHelperImpl;
import com.newland.tianyan.face.cache.MilvusKey;
import com.newland.tianyan.face.config.RabbitMQSender;
import com.newland.tianyan.face.constant.RabbitMqQueueName;
import com.newland.tianyan.face.constant.StatusConstants;
import com.newland.tianyan.face.dao.GroupInfoMapper;
import com.newland.tianyan.face.dao.UserInfoMapper;
import com.newland.tianyan.face.domain.Face;
import com.newland.tianyan.face.domain.GroupInfo;
import com.newland.tianyan.face.domain.UserInfo;
import com.newland.tianyan.face.vo.FaceSetFaceCompareVo;
import com.newland.tianyan.face.vo.FaceSetFaceDetectVo;
import com.newland.tianyan.face.vo.FaceSetFaceSearchVo;
import com.newland.tianyan.face.vo.FaceDetectVo;
import com.newland.tianyan.face.service.FacesetFaceService;
import com.newland.tianyan.face.utils.CosineDistanceTool;
import com.newland.tianyan.face.utils.FeaturesTool;
import newlandFace.NLFace;
import org.apache.commons.lang3.ArrayUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.io.*;
import java.util.*;

import static java.lang.Math.abs;

/**
 * @Author: huangJunJie  2020-11-06 09:09
 */
@Service
public class FacesetFaceServiceImpl implements FacesetFaceService {

    @Autowired
    private RabbitMQSender rabbitMQSender;
    @Autowired
    private GroupInfoMapper groupInfoMapper;
    @Autowired
    private UserInfoMapper userInfoMapper;
    @Autowired
    private FaceCacheHelperImpl<Face> faceFaceCacheHelper;
    @Autowired
    private ImageStoreFeignService imageStorageService;
    private final static Map<String, Integer> TASK_TYPE = new HashMap<String, Integer>() {{
        put("coordinate", 1);
        put("feature", 2);
        put("liveness", 3);
        put("multiAttribute", 4);
    }};

    @Override
    public NLFace.CloudFaceSendMessage searchNew(FaceSetFaceSearchVo request) {
        String fileName = request.getImage();
        List<String> groupIdList = new ArrayList<>();
        Collections.addAll(groupIdList, request.getGroupId().split(","));
        List<Long> groupList = new ArrayList<>(groupIdList.size());
        for (String groupId : groupIdList) {
            GroupInfo groupInfo = new GroupInfo();
            groupInfo.setAppId(request.getAppId());
            groupInfo.setGroupId(groupId);
            groupInfo.setIsDelete(StatusConstants.NOT_DELETE);
            //仅筛选出非逻辑删除的用户组
            GroupInfo group = groupInfoMapper.selectOne(groupInfo);
            if (group != null) {
                groupList.add(group.getId());
            }
        }
        //已获得特征值，计算向量匹配用户
        NLFace.CloudFaceSendMessage feature =
                amqpHelper(fileName, request.getMaxFaceNum(), TASK_TYPE.get("feature"));
        List<Float> featureRaw = FeaturesTool.normalizeConvertToList(feature.getFeatureResultList().get(0).getFeaturesList());
        //从缓存中拿到符合向量TopN个的用户
        List<QueryRes> queryFaceList = faceFaceCacheHelper.query(request.getAppId(), featureRaw, groupList, request.getMaxUserNum());

        //封装结果集
        NLFace.CloudFaceSendMessage.Builder resultBuilder = NLFace.CloudFaceSendMessage.newBuilder();
        resultBuilder.setLogId(UUID.randomUUID().toString());
        resultBuilder.setFaceNum(feature.getFaceNum());
        //结果集.UserResult
        for (QueryRes milvusQueryRes : queryFaceList) {
            //拆解根据规则拼接的向量id获得gid、uid
            Long vectorId = milvusQueryRes.getEntityId();
            Long gid = MilvusKey.splitGid(vectorId);
            Long uid = MilvusKey.splitUid(vectorId);
            NLFace.CloudFaceSearchResult.Builder builder = resultBuilder.addUserResultBuilder();
            //用户信息
            UserInfo conditionUser = new UserInfo();
            conditionUser.setId(uid);
            UserInfo queryUser = userInfoMapper.selectOne(conditionUser);
            if (queryUser == null) {
                continue;
            }
            builder.setUserId(queryUser.getUserId());
            builder.setUserName(queryUser.getUserName());
            builder.setUserInfo(queryUser.getUserInfo());
            //用户组信息
            GroupInfo conditionGroup = new GroupInfo();
            conditionGroup.setId(gid);
            GroupInfo queryGroup = groupInfoMapper.selectOne(conditionGroup);
            if (queryGroup == null) {
                continue;
            }
            builder.setGroupId(queryGroup.getGroupId());
            //置信度
            builder.setConfidence(String.valueOf(milvusQueryRes.getDistance()));
        }
        // 如果face_fields有值(coordinate,liveness)，将分析的结果也加进来
        this.faceFieldHelper(request.getFaceFields(), resultBuilder, fileName);
        return resultBuilder.build();
    }

    private NLFace.CloudFaceSendMessage amqpHelper(String fileName, int maxFaceNum, Integer taskType) {
        // get features
        String logId = UUID.randomUUID().toString();
        NLFace.CloudFaceAllRequest.Builder amqpRequest = NLFace.CloudFaceAllRequest.newBuilder();
        amqpRequest.setLogId(logId);
        amqpRequest.setTaskType(taskType);
        amqpRequest.setImage(fileName);
        amqpRequest.setMaxFaceNum(maxFaceNum);
        byte[] message = amqpRequest.build().toByteArray();
        if (taskType == -20) {
            return JSONSendHelper(RabbitMqQueueName.FACE_DETECT_QUEUE_V20_OLD, message);
        }
        if (taskType == 20) {
            return JSONSendHelper(RabbitMqQueueName.FACE_DETECT_QUEUE_V20, message);
        }
        if (taskType == 36) {
            return JSONSendHelper(RabbitMqQueueName.FACE_DETECT_QUEUE_V36, message);
        }
        if (taskType == 34) {
            return JSONSendHelper(RabbitMqQueueName.FACE_DETECT_QUEUE_V34, message);
        }
        return JSONSendHelper(RabbitMqQueueName.FACE_DETECT_QUEUE, message);
    }

    private NLFace.CloudFaceSendMessage JSONSendHelper(String routingKey, byte[] msg) {
        // get message
        byte[] data = rabbitMQSender.send(routingKey, msg);
        String json = new String(data);

        NLFace.CloudFaceSendMessage.Builder result = NLFace.CloudFaceSendMessage.newBuilder();
        try {
            JsonFormat.merge(json, result);
        } catch (JsonFormat.ParseException e) {
            e.printStackTrace();
            throw new CommonException(6400, "proto parse exception");
        }
        NLFace.CloudFaceSendMessage build = result.build();
        if (!StringUtils.isEmpty(build.getErrorMsg())) {
            throw new CommonException(build.getErrorCode(), build.getErrorMsg());
        }
        return build;
    }


    public void faceFieldHelper(String faceFieldsStr, NLFace.CloudFaceSendMessage.Builder result,
                                String fileName) {
        if (!StringUtils.isEmpty(faceFieldsStr)) {
            String[] faceFields = faceFieldsStr.split(",");
            for (String faceField : faceFields) {
                if (TASK_TYPE.get(faceField) != null) {
                    switch (faceField) {
                        case "coordinate":
                            NLFace.CloudFaceSendMessage msg1 =
                                    amqpHelper(fileName, 1, (Integer) TASK_TYPE.get(faceField));
                            result.addFaceInfos(msg1.getFaceInfos(0).toBuilder());
                            break;
                        case "liveness":
                            NLFace.CloudFaceSendMessage msg3 =
                                    amqpHelper(fileName, 1, (Integer) TASK_TYPE.get(faceField));
                            result.addLivenessResult(msg3.getLivenessResult(0).toBuilder());
                            break;
                        default: {
                            break;
                        }
                    }
                }
            }
        }
    }

    @Override
    public NLFace.CloudFaceSendMessage compare(FaceSetFaceCompareVo request) {
        String image1 = request.getFirstImage();
        String image2 = request.getSecondImage();
        String logId = UUID.randomUUID().toString();
        UploadReq uploadReq1 = UploadReq.builder().image(image1).build();
        imageStorageService.uploadImageV2(uploadReq1);
        UploadReq uploadReq2 = UploadReq.builder().image(image1).build();
        imageStorageService.uploadImageV2(uploadReq2);

        NLFace.CloudFaceSendMessage feature1 = amqpHelper(image1, 1, (Integer) TASK_TYPE.get("feature"));
        NLFace.CloudFaceSendMessage feature2 = amqpHelper(image2, 1, (Integer) TASK_TYPE.get("feature"));
        List<Float> featuresList1 = feature1.getFeatureResultList().get(0).getFeaturesList();
        List<Float> featuresList2 = feature2.getFeatureResultList().get(0).getFeaturesList();

        float distance = CosineDistanceTool.getNormalDistance(ArrayUtils.toPrimitive(featuresList1.toArray(new Float[0]), 0.0F), ArrayUtils.toPrimitive(featuresList2.toArray(new Float[0]), 0.0F));

        NLFace.CloudFaceSendMessage.Builder result = NLFace.CloudFaceSendMessage.newBuilder();
        result.setConfidence(distance);
        result.setLogId(logId);

        faceFieldHelper(request.getFaceFields(), result, image1);
        faceFieldHelper(request.getFaceFields(), result, image2);
        return result.build();
    }

    @Override
    public NLFace.CloudFaceSendMessage multiAttribute(FaceDetectVo vo) {
        String image = vo.getImage();
        String logId = UUID.randomUUID().toString();
        UploadReq uploadReq = UploadReq.builder().image(image).build();
        imageStorageService.uploadImageV2(uploadReq);

        NLFace.CloudFaceSendMessage.Builder builder = NLFace.CloudFaceSendMessage.newBuilder();

        String faceFieldsStr = vo.getFaceFields();
        if (!StringUtils.isEmpty(faceFieldsStr)) {
            String[] faceFields = faceFieldsStr.split(",");
            for (String faceField : faceFields) {
                if (TASK_TYPE.get(faceField) != null) {
                    NLFace.CloudFaceSendMessage msg =
                            amqpHelper(image, vo.getMaxFaceNum(), (Integer) TASK_TYPE.get(faceField));
                    builder.mergeFrom(msg);
                }
            }
        }
        NLFace.CloudFaceSendMessage def =
                amqpHelper(image, vo.getMaxFaceNum(), (Integer) TASK_TYPE.get("multiAttribute"));
        builder.mergeFrom(def);
        builder.setLogId(logId);
        return builder.build();
    }

    @Override
    public NLFace.CloudFaceSendMessage liveness(FaceDetectVo vo) {
        String image = vo.getImage();
        String logId = UUID.randomUUID().toString();
        UploadReq uploadReq = UploadReq.builder().image(image).build();
        imageStorageService.uploadImageV2(uploadReq);

        NLFace.CloudFaceSendMessage.Builder builder = NLFace.CloudFaceSendMessage.newBuilder();

        String faceFieldsStr = vo.getFaceFields();
        if (!StringUtils.isEmpty(faceFieldsStr)) {
            String[] faceFields = faceFieldsStr.split(",");
            for (String faceField : faceFields) {
                if (TASK_TYPE.get(faceField) != null) {
                    NLFace.CloudFaceSendMessage msg =
                            amqpHelper(image, vo.getMaxFaceNum(), (Integer) TASK_TYPE.get(faceField));
                    builder.mergeFrom(msg);
                }
            }
        }
        NLFace.CloudFaceSendMessage def =
                amqpHelper(image, vo.getMaxFaceNum(), (Integer) TASK_TYPE.get("liveness"));
        builder.mergeFrom(def);
        builder.setLogId(logId);
        return builder.build();
    }

    @Override
    public NLFace.CloudFaceSendMessage detect(FaceSetFaceDetectVo request) {
        String image = request.getImage();
        String logId = UUID.randomUUID().toString();
        UploadReq uploadReq = UploadReq.builder().image(image).build();
        imageStorageService.uploadImageV2(uploadReq);

        NLFace.CloudFaceSendMessage.Builder builder = NLFace.CloudFaceSendMessage.newBuilder();

        String faceFieldsStr = request.getFaceFields();
        if (!StringUtils.isEmpty(faceFieldsStr)) {
            String[] faceFields = faceFieldsStr.split(",");
            for (String faceField : faceFields) {
                if (TASK_TYPE.get(faceField) != null) {
                    NLFace.CloudFaceSendMessage msg =
                            amqpHelper(image, request.getMaxFaceNum(), (Integer) TASK_TYPE.get(faceField));
                    builder.mergeFrom(msg);
                }
            }
        }
        NLFace.CloudFaceSendMessage def =
                amqpHelper(image, request.getMaxFaceNum(), (Integer) TASK_TYPE.get("coordinate"));
        builder.mergeFrom(def);
        builder.setLogId(logId);
        return builder.build();
    }

    @Override
    public NLFace.CloudFaceSendMessage features(NLBackend.BackendAllRequest receive, int model) {
        int qualityControl = receive.getQualityControl();
        if (qualityControl != 0) {
            NLFace.CloudFaceSendMessage.Builder detectBuilder = NLFace.CloudFaceSendMessage.newBuilder();
            NLFace.CloudFaceSendMessage detectDef =
                    amqpHelper(receive.getImage(), 1, 1);
            detectBuilder.mergeFrom(detectDef);
            NLFace.CloudFaceSendMessage detectRe = detectBuilder.build();
            double eyeDistance = Math.sqrt(Math.pow(detectRe.getFaceInfos(0).getPtx(0) - detectRe.getFaceInfos(0).getPtx(1), 2)
                    + Math.pow(detectRe.getFaceInfos(0).getPty(0) - detectRe.getFaceInfos(0).getPty(1), 2));
            if (qualityControl == 1 && eyeDistance <= 20) {
                throw new CommonException(6200, "low quality control fail! eye distance < 20 pixels");
            }
            if (qualityControl == 2 && eyeDistance <= 40) {
                throw new CommonException(6200, "median quality control fail! eye distance < 40 pixels");
            }
            if (qualityControl == 3 && eyeDistance <= 60) {
                throw new CommonException(6200, "high quality control fail! eye distance < 60 pixels");
            }
            NLFace.CloudFaceSendMessage.Builder builder = NLFace.CloudFaceSendMessage.newBuilder();
            NLFace.CloudFaceSendMessage def =
                    amqpHelper(receive.getImage(), 1, 4);
            builder.mergeFrom(def);
            NLFace.CloudFaceSendMessage qualityRe = builder.build();
            if (receive.getQualityControl() == 1) {
                if (abs(qualityRe.getFaceAttributes(0).getPitch()) >= 40) {
                    throw new CommonException(6200, "low quality control fail! abs(pitch) >= 40");
                }
                if (abs(qualityRe.getFaceAttributes(0).getYaw()) >= 40) {
                    throw new CommonException(6200, "low quality control fail! abs(yaw) >= 40");
                }
                if (abs(qualityRe.getFaceAttributes(0).getRoll()) >= 40) {
                    throw new CommonException(6200, "low quality control fail! abs(roll) >= 40");
                }
                if (qualityRe.getFaceAttributes(0).getBlur() >= 0.8) {
                    throw new CommonException(6200, "low quality control fail! blur >= 0.8");
                }
                if (qualityRe.getFaceAttributes(0).getOcclusion() >= 0.8) {
                    throw new CommonException(6200, "low quality control fail! occlusion >= 0.8");
                }
                if (qualityRe.getFaceAttributes(0).getBrightness() <= 0.15 || qualityRe.getFaceAttributes(0).getBrightness() >= 0.95) {
                    throw new CommonException(6200, "low quality control fail! brightness is not in (0.15, 0.95)");
                }
                if (qualityRe.getFaceAttributes(0).getBrightnessSideDiff() >= 0.8) {
                    throw new CommonException(6200, "low quality control fail! brightness side diff >= 0.8");
                }
                if (qualityRe.getFaceAttributes(0).getBrightnessUpdownDiff() >= 0.8) {
                    throw new CommonException(6200, "low quality control fail! brightness updown diff >= 0.8");
                }
                if (qualityRe.getFaceAttributes(0).getToneOffCenter() >= 0.8) {
                    throw new CommonException(6200, "low quality control fail! tone off center >= 0.8");
                }
            }
            if (receive.getQualityControl() == 2) {
                if (abs(qualityRe.getFaceAttributes(0).getPitch()) >= 30) {
                    throw new CommonException(6200, "median quality control fail! abs(pitch) >= 30");
                }
                if (abs(qualityRe.getFaceAttributes(0).getYaw()) >= 30) {
                    throw new CommonException(6200, "median quality control fail! abs(yaw) >= 30");
                }
                if (abs(qualityRe.getFaceAttributes(0).getRoll()) >= 30) {
                    throw new CommonException(6200, "median quality control fail! abs(roll) >= 30");
                }
                if (qualityRe.getFaceAttributes(0).getBlur() >= 0.6) {
                    throw new CommonException(6200, "median quality control fail! blur >= 0.6");
                }
                if (qualityRe.getFaceAttributes(0).getOcclusion() >= 0.6) {
                    throw new CommonException(6200, "median quality control fail! occlusion >= 0.6");
                }
                if (qualityRe.getFaceAttributes(0).getBrightness() <= 0.2 || qualityRe.getFaceAttributes(0).getBrightness() >= 0.9) {
                    throw new CommonException(6200, "median quality control fail! brightness is not in (0.2, 0.9)");
                }
                if (qualityRe.getFaceAttributes(0).getBrightnessSideDiff() >= 0.6) {
                    throw new CommonException(6200, "median quality control fail! brightness side diff >= 0.6");
                }
                if (qualityRe.getFaceAttributes(0).getBrightnessUpdownDiff() >= 0.6) {
                    throw new CommonException(6200, "median quality control fail! brightness updown diff >= 0.6");
                }
                if (qualityRe.getFaceAttributes(0).getToneOffCenter() >= 0.6) {
                    throw new CommonException(6200, "median quality control fail! tone off center >= 0.6");
                }
            }
            if (receive.getQualityControl() == 3) {
                if (abs(qualityRe.getFaceAttributes(0).getPitch()) >= 20) {
                    throw new CommonException(6200, "high quality control fail! abs(pitch) >= 20");
                }
                if (abs(qualityRe.getFaceAttributes(0).getYaw()) >= 20) {
                    throw new CommonException(6200, "high quality control fail! abs(yaw) >= 20");
                }
                if (abs(qualityRe.getFaceAttributes(0).getRoll()) >= 20) {
                    throw new CommonException(6200, "high quality control fail! abs(roll) >= 20");
                }
                if (qualityRe.getFaceAttributes(0).getBlur() >= 0.4) {
                    throw new CommonException(6200, "high quality control fail! blur >= 0.4");
                }
                if (qualityRe.getFaceAttributes(0).getOcclusion() >= 0.4) {
                    throw new CommonException(6200, "high quality control fail! occlusion >= 0.4");
                }
                if (qualityRe.getFaceAttributes(0).getBrightness() <= 0.3 || qualityRe.getFaceAttributes(0).getBrightness() >= 0.8) {
                    throw new CommonException(6200, "high quality control fail! brightness is not in (0.3, 0.8)");
                }
                if (qualityRe.getFaceAttributes(0).getBrightnessSideDiff() >= 0.4) {
                    throw new CommonException(6200, "high quality control fail! brightness side diff >= 0.4");
                }
                if (qualityRe.getFaceAttributes(0).getBrightnessUpdownDiff() >= 0.4) {
                    throw new CommonException(6200, "high quality control fail! brightness updown diff >= 0.4");
                }
                if (qualityRe.getFaceAttributes(0).getToneOffCenter() >= 0.4) {
                    throw new CommonException(6200, "high quality control fail! tone off center >= 0.4");
                }
            }
        }
        Face query = ProtobufUtils.parseTo(receive, Face.class);
        Face temp = getByImage(query.getImage(), model);
        temp.setUserId(receive.getUserId());
        temp.setAppId(receive.getAppId());
        if (model == -20) {
            NLFace.CloudFaceSendMessage.Builder result = NLFace.CloudFaceSendMessage.newBuilder();
            result.setLogId(LogUtils.getLogId());
            ObjectInputStream in;
            float[] features = new float[512];
            try {
                in = new ObjectInputStream(new ByteArrayInputStream(temp.getFeatures()));
                features = (float[]) in.readObject();
            } catch (IOException | ClassNotFoundException e) {
                e.printStackTrace();
            }
            NLFace.CloudFaceFeatureResult.Builder builder = result.addFeatureResultBuilder();
            for (int i = 0; i < 512; i++) {
                builder.addFeatures(features[i]);
            }
            NLFace.CloudFaceSendMessage build = result.build();
            if (!StringUtils.isEmpty(build.getErrorMsg())) {
                throw new CommonException(build.getErrorCode(), build.getErrorMsg());
            }
            return build;
        }

        NLFace.CloudFaceSendMessage.Builder result = NLFace.CloudFaceSendMessage.newBuilder();
        result.setLogId(LogUtils.getLogId());
        result.setFeature(temp.getFeaturesNew());
        result.setVersion(temp.getVersion());
        NLFace.CloudFaceSendMessage build = result.build();
        if (!StringUtils.isEmpty(build.getErrorMsg())) {
            throw new CommonException(build.getErrorCode(), build.getErrorMsg());
        }
        return build;
    }

    public Face getByImage(String image, int model) {
        UploadReq uploadReq = UploadReq.builder().image(image).build();
        String imagePath = imageStorageService.uploadImageV2(uploadReq).getImagePath();

        NLFace.CloudFaceSendMessage feature = amqpHelper(image, 1, model);

        if (model == -20) {
            NLFace.CloudFaceSendMessage.Builder builder = feature.toBuilder();
            Face face = new Face();
            face.setFeaturesNew(feature.getFeature());
            face.setVersion(feature.getVersion());
            face.setImagePath(imagePath);
            List<Float> preFeature = builder.getFeatureResult(0).getFeaturesList();
            float[] afterFeature = new float[preFeature.size()];
            float tempSum = 0;
            float tempAdd = 0;
            for (int i = 0; i < preFeature.size(); i++) {
                tempSum += preFeature.get(i) * preFeature.get(i);
            }
            for (int i = 0; i < preFeature.size(); i++) {
                tempAdd = preFeature.get(i) / (float) Math.sqrt(tempSum);
                afterFeature[i] = tempAdd;
            }
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            ObjectOutputStream outputStream = null;
            try {
                outputStream = new ObjectOutputStream(out);
                outputStream.writeObject(afterFeature);
                byte[] bytes = out.toByteArray();
                face.setFeatures(bytes);
                outputStream.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return face;
        }
        Face face = new Face();
        face.setFeaturesNew(feature.getFeature());
        face.setVersion(feature.getVersion());
        face.setImagePath(imagePath);
        return face;
    }
}
