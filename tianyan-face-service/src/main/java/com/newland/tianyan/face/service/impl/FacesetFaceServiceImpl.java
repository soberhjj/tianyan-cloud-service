package com.newland.tianyan.face.service.impl;

import com.googlecode.protobuf.format.JsonFormat;
import com.newland.tianya.commons.base.exception.BaseException;
import com.newland.tianya.commons.base.model.imagestrore.UploadReqDTO;
import com.newland.tianya.commons.base.model.proto.NLBackend;
import com.newland.tianya.commons.base.model.proto.NLFace;
import com.newland.tianya.commons.base.model.vectorsearch.QueryResDTO;
import com.newland.tianya.commons.base.support.ExceptionSupport;
import com.newland.tianya.commons.base.utils.*;
import com.newland.tianyan.face.constant.EntityStatusConstants;
import com.newland.tianyan.face.constant.ExceptionEnum;
import com.newland.tianyan.face.dao.GroupInfoMapper;
import com.newland.tianyan.face.dao.UserInfoMapper;
import com.newland.tianyan.face.domain.dto.FaceDetectReqDTO;
import com.newland.tianyan.face.domain.dto.FaceSetFaceCompareReqDTO;
import com.newland.tianyan.face.domain.dto.FaceSetFaceDetectReqDTO;
import com.newland.tianyan.face.domain.dto.FaceSetFaceSearchReqDTO;
import com.newland.tianyan.face.domain.entity.FaceDO;
import com.newland.tianyan.face.domain.entity.GroupInfoDO;
import com.newland.tianyan.face.domain.entity.UserInfoDO;
import com.newland.tianyan.face.feign.client.ImageStoreFeignService;
import com.newland.tianyan.face.mq.RabbitMQSender;
import com.newland.tianyan.face.mq.RabbitMqQueueName;
import com.newland.tianyan.face.service.FacesetFaceService;
import com.newland.tianyan.face.utils.VectorSearchKeyUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ArrayUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;
import java.io.*;
import java.util.*;

import static com.newland.tianyan.face.constant.BusinessArgumentConstants.FACE_FIELD_COORDINATE;
import static com.newland.tianyan.face.constant.BusinessArgumentConstants.FACE_FIELD_LIVENESS;
import static java.lang.Math.abs;

/**
 * @Author: huangJunJie  2020-11-06 09:09
 */
@Service
@RefreshScope
@Slf4j
public class FacesetFaceServiceImpl implements FacesetFaceService {

    @Autowired
    private RabbitMQSender rabbitMqSender;
    @Autowired
    private GroupInfoMapper groupInfoMapper;
    @Autowired
    private UserInfoMapper userInfoMapper;
    @Autowired
    private VectorSearchServiceImpl<FaceDO> faceFaceCacheHelper;
    @Autowired
    private ImageStoreFeignService imageStorageService;

    @Value("${enable-image-storage}")
    private boolean enableImageStorage;

    private final static Map<String, Integer> TASK_TYPE = new HashMap<String, Integer>() {{
        put("coordinate", 1);
        put("feature", 2);
        put("liveness", 3);
        put("multiAttribute", 4);
    }};

    @Override
    public NLFace.CloudFaceSendMessage searchNew(FaceSetFaceSearchReqDTO request) throws BaseException {
        String fileName = request.getImage();
        log.info("人脸搜索，开始检查图片有效性");
        ImageCheckUtils.imageCheck(fileName);
        List<String> groupIdList = new ArrayList<>();
        Collections.addAll(groupIdList, request.getGroupId().split(","));
        log.info("人脸搜索，输入用户组{},进入用户组有效性筛查：用户组存在且不为空", groupIdList);
        List<Long> groupList = new ArrayList<>(groupIdList.size());
        for (String groupId : groupIdList) {
            GroupInfoDO groupInfoDO = new GroupInfoDO();
            groupInfoDO.setAppId(request.getAppId());
            groupInfoDO.setGroupId(groupId);
            groupInfoDO.setIsDelete(EntityStatusConstants.NOT_DELETE);
            GroupInfoDO group = groupInfoMapper.selectOne(groupInfoDO);
            if (group != null) {
                if (group.getUserNumber() == 0) {
                    throw ExceptionSupport.toException(ExceptionEnum.EMPTY_GROUP, groupId);
                }
                groupList.add(group.getId());
            } else {
                throw ExceptionSupport.toException(ExceptionEnum.GROUP_NOT_FOUND, groupId);
            }
        }
        log.info("人脸搜索，用户组筛查通过，请求计算图片特征值");
        NLFace.CloudFaceSendMessage feature =
                amqpHelper(fileName, request.getMaxFaceNum(), TASK_TYPE.get("feature"));
        List<Float> featureRaw = FeaturesTool.normalizeConvertToList(feature.getFeatureResultList().get(0).getFeaturesList());
        log.info("人脸搜索，图片特征值已获得，开始向量搜索中。");
        List<QueryResDTO> queryFaceList = faceFaceCacheHelper.query(request.getAppId(), featureRaw, request.getMaxUserNum());
        if (CollectionUtils.isEmpty(queryFaceList)) {
            throw ExceptionSupport.toException(ExceptionEnum.FACE_NOT_FOUND);
        }
        log.info("人脸搜索，向量搜索得到结果，开始筛选结果集并封装");
        NLFace.CloudFaceSendMessage.Builder resultBuilder = NLFace.CloudFaceSendMessage.newBuilder();
        resultBuilder.setLogId(UUID.randomUUID().toString());
        resultBuilder.setFaceNum(feature.getFaceNum());
        int unUseResult = 0;
        for (QueryResDTO milvusQueryRes : queryFaceList) {
            //拆解根据规则拼接的向量id获得gid、uid
            Long vectorId = milvusQueryRes.getEntityId();
            Long gid = VectorSearchKeyUtils.splitGid(vectorId);
            Long uid = VectorSearchKeyUtils.splitUid(vectorId);
            NLFace.CloudFaceSearchResult.Builder builder = resultBuilder.addUserResultBuilder();
            //人脸筛选
            if (!groupList.contains(gid)) {
                log.info("剔除非当前请求的用户组结果gid:{}", gid);
                unUseResult++;
                continue;
            }
            //用户组信息
            GroupInfoDO conditionGroup = new GroupInfoDO();
            conditionGroup.setId(gid);
            GroupInfoDO queryGroup = groupInfoMapper.selectOne(conditionGroup);
            if (queryGroup == null) {
                log.info("剔除db已删除用户组信息gid:{}", gid);
                unUseResult++;
                continue;
            }
            builder.setGroupId(queryGroup.getGroupId());
            //用户信息
            UserInfoDO conditionUser = new UserInfoDO();
            conditionUser.setId(uid);
            UserInfoDO queryUser = userInfoMapper.selectOne(conditionUser);
            if (queryUser == null) {
                log.info("剔除db已删除用户信息uid:{}", uid);
                unUseResult++;
                continue;
            }
            builder.setUserId(queryUser.getUserId());
            builder.setUserName(queryUser.getUserName());
            builder.setUserInfo(queryUser.getUserInfo());
            //置信度
            builder.setConfidence(String.valueOf(milvusQueryRes.getDistance()));
        }
        if (unUseResult == queryFaceList.size()) {
            throw ExceptionSupport.toException(ExceptionEnum.FACE_NOT_FOUND);
        }
        log.info("人脸搜索-搜索已结束，开始请求活体或5~106点坐标");
        this.checkFaceField(request.getFaceFields());
        this.faceFieldHelper(request.getFaceFields(), resultBuilder, fileName);
        return resultBuilder.build();
    }

    private NLFace.CloudFaceSendMessage amqpHelper(String fileName, int maxFaceNum, Integer taskType) throws BaseException {
        // get features
        String logId = UUID.randomUUID().toString();
        NLFace.CloudFaceAllRequest.Builder amqpRequest = NLFace.CloudFaceAllRequest.newBuilder();
        amqpRequest.setLogId(logId);
        amqpRequest.setTaskType(taskType);
        amqpRequest.setImage(fileName);
        amqpRequest.setMaxFaceNum(maxFaceNum);
        byte[] message = amqpRequest.build().toByteArray();
        if (taskType == -20) {
            return jsonSendHelper(RabbitMqQueueName.FACE_DETECT_QUEUE_V20_OLD, message);
        }
        if (taskType == 20) {
            return jsonSendHelper(RabbitMqQueueName.FACE_DETECT_QUEUE_V20, message);
        }
        if (taskType == 36) {
            return jsonSendHelper(RabbitMqQueueName.FACE_DETECT_QUEUE_V36, message);
        }
        if (taskType == 34) {
            return jsonSendHelper(RabbitMqQueueName.FACE_DETECT_QUEUE_V34, message);
        }
        return jsonSendHelper(RabbitMqQueueName.FACE_DETECT_QUEUE, message);
    }

    private NLFace.CloudFaceSendMessage jsonSendHelper(String routingKey, byte[] msg) {
        // get message
        byte[] data = rabbitMqSender.send(routingKey, msg);
        String json = new String(data);

        NLFace.CloudFaceSendMessage.Builder result = NLFace.CloudFaceSendMessage.newBuilder();
        try {
            JsonFormat.merge(json, result);
        } catch (JsonFormat.ParseException e) {
            throw ExceptionSupport.toException(ExceptionEnum.PROTO_PARSE_ERROR, e);

        }
        NLFace.CloudFaceSendMessage build = result.build();
        if (!StringUtils.isEmpty(build.getErrorMsg())) {
            throw new BaseException(build.getErrorCode(), build.getErrorMsg());
        }
        return build;
    }


    public void faceFieldHelper(String faceFieldsStr, NLFace.CloudFaceSendMessage.Builder result,
                                String fileName) throws BaseException {
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
                        default:
                            break;
                    }
                }
            }
        }
    }

    @Override
    public NLFace.CloudFaceSendMessage compare(FaceSetFaceCompareReqDTO request) throws IOException {
        String image1 = request.getFirstImage();
        String image2 = request.getSecondImage();
        //检查图片
        ImageCheckUtils.imageCheck(image1);
        ImageCheckUtils.imageCheck(image2);
        //是否异步存储图片
        if (enableImageStorage) {
            imageStorageService.asyncUpload(UploadReqDTO.builder().image(image1).build());
            imageStorageService.asyncUpload(UploadReqDTO.builder().image(image2).build());
        }

        String logId = UUID.randomUUID().toString();

        NLFace.CloudFaceSendMessage feature1 = amqpHelper(image1, 1, (Integer) TASK_TYPE.get("feature"));
        NLFace.CloudFaceSendMessage feature2 = amqpHelper(image2, 1, (Integer) TASK_TYPE.get("feature"));
        List<Float> featuresList1 = feature1.getFeatureResultList().get(0).getFeaturesList();
        List<Float> featuresList2 = feature2.getFeatureResultList().get(0).getFeaturesList();

        float distance = CosineDistanceTool.getNormalDistance(ArrayUtils.toPrimitive(featuresList1.toArray(new Float[0]), 0.0F), ArrayUtils.toPrimitive(featuresList2.toArray(new Float[0]), 0.0F));

        NLFace.CloudFaceSendMessage.Builder result = NLFace.CloudFaceSendMessage.newBuilder();
        result.setConfidence(distance);
        result.setLogId(logId);

        this.checkFaceField(request.getFaceFields());
        faceFieldHelper(request.getFaceFields(), result, image1);
        faceFieldHelper(request.getFaceFields(), result, image2);
        return result.build();
    }

    @Override
    public NLFace.CloudFaceSendMessage multiAttribute(FaceDetectReqDTO vo) throws IOException {
        String image = vo.getImage();
        //检查图片
        ImageCheckUtils.imageCheck(image);
        //是否异步存储图片
        if (enableImageStorage) {
            imageStorageService.asyncUpload(UploadReqDTO.builder().image(image).build());
        }

        String logId = UUID.randomUUID().toString();

        NLFace.CloudFaceSendMessage.Builder builder = NLFace.CloudFaceSendMessage.newBuilder();

        String faceFieldsStr = vo.getFaceFields();
        this.checkFaceField(faceFieldsStr);
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
    public NLFace.CloudFaceSendMessage liveness(FaceDetectReqDTO vo) throws IOException {
        String image = vo.getImage();
        //检查图片
        ImageCheckUtils.imageCheck(image);
        //是否异步存储图片
        if (enableImageStorage) {
            imageStorageService.asyncUpload(UploadReqDTO.builder().image(image).build());
        }

        String logId = UUID.randomUUID().toString();

        NLFace.CloudFaceSendMessage.Builder builder = NLFace.CloudFaceSendMessage.newBuilder();

        String faceFieldsStr = vo.getFaceFields();
        this.checkFaceField(faceFieldsStr);
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
    public NLFace.CloudFaceSendMessage detect(FaceSetFaceDetectReqDTO request) throws IOException {
        String image = request.getImage();
        //检查图片
        ImageCheckUtils.imageCheck(image);
        //是否异步存储图片
        if (enableImageStorage) {
            imageStorageService.asyncUpload(UploadReqDTO.builder().image(image).build());
        }

        String logId = UUID.randomUUID().toString();

        NLFace.CloudFaceSendMessage.Builder builder = NLFace.CloudFaceSendMessage.newBuilder();

        String faceFieldsStr = request.getFaceFields();
        this.checkFaceField(faceFieldsStr);
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
    public NLFace.CloudFaceSendMessage features(NLBackend.BackendAllRequest receive, int model) throws IOException {
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
                throw new BaseException(6200, "low quality control fail! eye distance < 20 pixels");
            }
            if (qualityControl == 2 && eyeDistance <= 40) {
                throw new BaseException(6200, "median quality control fail! eye distance < 40 pixels");
            }
            if (qualityControl == 3 && eyeDistance <= 60) {
                throw new BaseException(6200, "high quality control fail! eye distance < 60 pixels");
            }
            NLFace.CloudFaceSendMessage.Builder builder = NLFace.CloudFaceSendMessage.newBuilder();
            NLFace.CloudFaceSendMessage def =
                    amqpHelper(receive.getImage(), 1, 4);
            builder.mergeFrom(def);
            NLFace.CloudFaceSendMessage qualityRe = builder.build();
            if (receive.getQualityControl() == 1) {
                if (abs(qualityRe.getFaceAttributes(0).getPitch()) >= 40) {
                    throw new BaseException(6200, "low quality control fail! abs(pitch) >= 40");
                }
                if (abs(qualityRe.getFaceAttributes(0).getYaw()) >= 40) {
                    throw new BaseException(6200, "low quality control fail! abs(yaw) >= 40");
                }
                if (abs(qualityRe.getFaceAttributes(0).getRoll()) >= 40) {
                    throw new BaseException(6200, "low quality control fail! abs(roll) >= 40");
                }
                if (qualityRe.getFaceAttributes(0).getBlur() >= 0.8) {
                    throw new BaseException(6200, "low quality control fail! blur >= 0.8");
                }
                if (qualityRe.getFaceAttributes(0).getOcclusion() >= 0.8) {
                    throw new BaseException(6200, "low quality control fail! occlusion >= 0.8");
                }
                if (qualityRe.getFaceAttributes(0).getBrightness() <= 0.15 || qualityRe.getFaceAttributes(0).getBrightness() >= 0.95) {
                    throw new BaseException(6200, "low quality control fail! brightness is not in (0.15, 0.95)");
                }
                if (qualityRe.getFaceAttributes(0).getBrightnessSideDiff() >= 0.8) {
                    throw new BaseException(6200, "low quality control fail! brightness side diff >= 0.8");
                }
                if (qualityRe.getFaceAttributes(0).getBrightnessUpdownDiff() >= 0.8) {
                    throw new BaseException(6200, "low quality control fail! brightness updown diff >= 0.8");
                }
                if (qualityRe.getFaceAttributes(0).getToneOffCenter() >= 0.8) {
                    throw new BaseException(6200, "low quality control fail! tone off center >= 0.8");
                }
            }
            if (receive.getQualityControl() == 2) {
                if (abs(qualityRe.getFaceAttributes(0).getPitch()) >= 30) {
                    throw new BaseException(6200, "median quality control fail! abs(pitch) >= 30");
                }
                if (abs(qualityRe.getFaceAttributes(0).getYaw()) >= 30) {
                    throw new BaseException(6200, "median quality control fail! abs(yaw) >= 30");
                }
                if (abs(qualityRe.getFaceAttributes(0).getRoll()) >= 30) {
                    throw new BaseException(6200, "median quality control fail! abs(roll) >= 30");
                }
                if (qualityRe.getFaceAttributes(0).getBlur() >= 0.6) {
                    throw new BaseException(6200, "median quality control fail! blur >= 0.6");
                }
                if (qualityRe.getFaceAttributes(0).getOcclusion() >= 0.6) {
                    throw new BaseException(6200, "median quality control fail! occlusion >= 0.6");
                }
                if (qualityRe.getFaceAttributes(0).getBrightness() <= 0.2 || qualityRe.getFaceAttributes(0).getBrightness() >= 0.9) {
                    throw new BaseException(6200, "median quality control fail! brightness is not in (0.2, 0.9)");
                }
                if (qualityRe.getFaceAttributes(0).getBrightnessSideDiff() >= 0.6) {
                    throw new BaseException(6200, "median quality control fail! brightness side diff >= 0.6");
                }
                if (qualityRe.getFaceAttributes(0).getBrightnessUpdownDiff() >= 0.6) {
                    throw new BaseException(6200, "median quality control fail! brightness updown diff >= 0.6");
                }
                if (qualityRe.getFaceAttributes(0).getToneOffCenter() >= 0.6) {
                    throw new BaseException(6200, "median quality control fail! tone off center >= 0.6");
                }
            }
            if (receive.getQualityControl() == 3) {
                if (abs(qualityRe.getFaceAttributes(0).getPitch()) >= 20) {
                    throw new BaseException(6200, "high quality control fail! abs(pitch) >= 20");
                }
                if (abs(qualityRe.getFaceAttributes(0).getYaw()) >= 20) {
                    throw new BaseException(6200, "high quality control fail! abs(yaw) >= 20");
                }
                if (abs(qualityRe.getFaceAttributes(0).getRoll()) >= 20) {
                    throw new BaseException(6200, "high quality control fail! abs(roll) >= 20");
                }
                if (qualityRe.getFaceAttributes(0).getBlur() >= 0.4) {
                    throw new BaseException(6200, "high quality control fail! blur >= 0.4");
                }
                if (qualityRe.getFaceAttributes(0).getOcclusion() >= 0.4) {
                    throw new BaseException(6200, "high quality control fail! occlusion >= 0.4");
                }
                if (qualityRe.getFaceAttributes(0).getBrightness() <= 0.3 || qualityRe.getFaceAttributes(0).getBrightness() >= 0.8) {
                    throw new BaseException(6200, "high quality control fail! brightness is not in (0.3, 0.8)");
                }
                if (qualityRe.getFaceAttributes(0).getBrightnessSideDiff() >= 0.4) {
                    throw new BaseException(6200, "high quality control fail! brightness side diff >= 0.4");
                }
                if (qualityRe.getFaceAttributes(0).getBrightnessUpdownDiff() >= 0.4) {
                    throw new BaseException(6200, "high quality control fail! brightness updown diff >= 0.4");
                }
                if (qualityRe.getFaceAttributes(0).getToneOffCenter() >= 0.4) {
                    throw new BaseException(6200, "high quality control fail! tone off center >= 0.4");
                }
            }
        }
        FaceDO query = ProtobufUtils.parseTo(receive, FaceDO.class);
        FaceDO temp = getByImage(query.getImage(), model);
        temp.setUserId(receive.getUserId());
        temp.setAppId(receive.getAppId());
        if (model == -20) {
            NLFace.CloudFaceSendMessage.Builder result = NLFace.CloudFaceSendMessage.newBuilder();
            result.setLogId(LogIdUtils.traceId());
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
                throw new BaseException(build.getErrorCode(), build.getErrorMsg());
            }
            return build;
        }

        NLFace.CloudFaceSendMessage.Builder result = NLFace.CloudFaceSendMessage.newBuilder();
        result.setLogId(LogIdUtils.traceId());
        result.setFeature(temp.getFeaturesNew());
        result.setVersion(temp.getVersion());
        NLFace.CloudFaceSendMessage build = result.build();
        if (!StringUtils.isEmpty(build.getErrorMsg())) {
            throw new BaseException(build.getErrorCode(), build.getErrorMsg());
        }
        return build;
    }

    public FaceDO getByImage(String image, int model) throws IOException {
        //检查图片
        ImageCheckUtils.imageCheck(image);
        //是否异步存储图片
        if (enableImageStorage) {
            imageStorageService.asyncUpload(UploadReqDTO.builder().image(image).build());
        }

        NLFace.CloudFaceSendMessage feature = amqpHelper(image, 1, model);

        if (model == -20) {
            NLFace.CloudFaceSendMessage.Builder builder = feature.toBuilder();
            FaceDO faceDO = new FaceDO();
            faceDO.setFeaturesNew(feature.getFeature());
            faceDO.setVersion(feature.getVersion());
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
                faceDO.setFeatures(bytes);
                outputStream.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return faceDO;
        }
        FaceDO faceDO = new FaceDO();
        faceDO.setFeaturesNew(feature.getFeature());
        faceDO.setVersion(feature.getVersion());
        return faceDO;
    }

    private void checkFaceField(String faceField) {
        if (StringUtils.isEmpty(faceField)) {
            return;
        }
        String[] faceFields = faceField.split(",");
        for (String item : faceFields) {
            boolean coordinate = FACE_FIELD_COORDINATE.equals(item);
            boolean liveNess = FACE_FIELD_LIVENESS.equals(item);
            if ((!coordinate) && (!liveNess)) {
                throw ExceptionSupport.toException(ExceptionEnum.WRONG_FACE_FIELD);
            }
        }
    }
}
