package com.newland.tianyan.face.service.impl;

import com.newland.tianya.commons.base.constants.GlobalExceptionEnum;
import com.newland.tianya.commons.base.exception.BaseException;
import com.newland.tianya.commons.base.model.imagestrore.UploadReqDTO;
import com.newland.tianya.commons.base.model.proto.NLBackend;
import com.newland.tianya.commons.base.model.proto.NLFace;
import com.newland.tianya.commons.base.model.vectorsearch.QueryResDTO;
import com.newland.tianya.commons.base.support.ExceptionSupport;
import com.newland.tianya.commons.base.utils.*;
import com.newland.tianyan.face.constant.ExceptionEnum;
import com.newland.tianyan.face.dao.UserInfoMapper;
import com.newland.tianyan.face.domain.dto.FaceDetectReqDTO;
import com.newland.tianyan.face.domain.dto.FaceSetFaceCompareReqDTO;
import com.newland.tianyan.face.domain.dto.FaceSetFaceSearchReqDTO;
import com.newland.tianyan.face.domain.entity.FaceDO;
import com.newland.tianyan.face.domain.entity.GroupInfoDO;
import com.newland.tianyan.face.domain.entity.UserInfoDO;
import com.newland.tianyan.face.domain.vo.FaceSearchVo;
import com.newland.tianyan.face.feign.client.ImageStoreFeignService;
import com.newland.tianyan.face.mq.IMqMessageService;
import com.newland.tianyan.face.service.FacesetFaceService;
import com.newland.tianyan.face.service.GroupInfoService;
import com.newland.tianyan.face.service.IQualityCheckService;
import com.newland.tianyan.face.service.IVectorSearchService;
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
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

import static com.newland.tianyan.face.constant.BusinessArgumentConstants.*;

/**
 * @Author: huangJunJie  2020-11-06 09:09
 */
@Service
@RefreshScope
@Slf4j
public class FacesetFaceServiceImpl implements FacesetFaceService {


    @Autowired
    private GroupInfoService groupInfoService;
    @Autowired
    private UserInfoMapper userInfoMapper;
    @Autowired
    private IVectorSearchService<FaceDO> faceFaceCacheHelper;
    @Autowired
    private ImageStoreFeignService imageStorageService;
    @Autowired
    private IQualityCheckService qualityCheckService;
    @Autowired
    private IMqMessageService mqMessageService;
    @Value("${enable-image-storage}")
    private boolean enableImageStorage;

    @Override
    public NLFace.CloudFaceSendMessage searchNew(FaceSetFaceSearchReqDTO request) throws BaseException {
        Set<String> faceFields = this.checkFaceFieldAndSplitToArray(request.getFaceFields());
        log.info("人脸搜索，输入用户组{},进入用户组有效性筛查：用户组存在且不为空", request.getGroupId());
        Map<Long, String> gidWithGroupIdMaps = this.convertToEffectiveGidMaps(request.getAppId(), request.getGroupId());
        log.info("人脸搜索，开始检查图片有效性");
        String image = request.getImage();
        ImageCheckUtils.imageCheck(image);
        UserInfoDO targetUser = null;
        String userId = request.getUserId();
        if (!StringUtils.isEmpty(userId)) {
            log.info("人脸搜索，当前为人脸认证逻辑");
            targetUser = this.getVerificationUserInfo(request.getAppId(), userId);
        }
        log.info("人脸搜索，用户组筛查通过，请求计算图片特征值");
        Integer taskType = mqMessageService.getTaskType(FACE_TASK_TYPE_FEATURE);
        NLFace.CloudFaceSendMessage feature =
                mqMessageService.amqpHelper(image, request.getMaxFaceNum(), taskType);
        List<Float> featureRaw = FeaturesTool.normalizeConvertToList(feature.getFeatureResultList().get(0).getFeaturesList());
        log.info("人脸搜索，图片特征值已获得，开始向量搜索中...");
        List<FaceSearchVo> queryFaceList = faceFaceCacheHelper.query(request.getAppId(), featureRaw);

        NLFace.CloudFaceSendMessage.Builder resultBuilder = NLFace.CloudFaceSendMessage.newBuilder();
        resultBuilder.setLogId(UUID.randomUUID().toString());
        resultBuilder.setFaceNum(feature.getFaceNum());
        if (!CollectionUtils.isEmpty(queryFaceList)) {
            log.info("人脸搜索，向量搜索得到结果，开始筛选结果集并封装");
            List<FaceSearchVo> effectiveFaceSearchVo = this.filterForValidGroup(gidWithGroupIdMaps, queryFaceList);
            this.checkVerificationUserInfo(effectiveFaceSearchVo, userId, targetUser);
            List<UserInfoDO> userInfoDOList = this.getValidUserInfo(request.getAppId(), effectiveFaceSearchVo);
            if (CollectionUtils.isEmpty(userInfoDOList)) {
                throw ExceptionSupport.toException(ExceptionEnum.FACE_NOT_MATCH);
            }
            int countTopK = 0;
            for (FaceSearchVo item : effectiveFaceSearchVo) {
                if (countTopK > request.getMaxFaceNum()) {
                    break;
                }
                for (UserInfoDO userInfoDO : userInfoDOList) {
                    if (item.getUid().equals(userInfoDO.getId())) {
                        NLFace.CloudFaceSearchResult.Builder builder = resultBuilder.addUserResultBuilder();
                        builder.setGroupId(gidWithGroupIdMaps.get(item.getGid()));
                        builder.setUserId(userInfoDO.getUserId());
                        builder.setUserName(userInfoDO.getUserName());
                        builder.setUserInfo(userInfoDO.getUserInfo());
                        //置信度
                        builder.setConfidence(item.getDistance());
                        countTopK++;
                        break;
                    }
                }
            }
            log.info("人脸搜索-搜索已结束，开始请求活体或5~106点坐标");
            this.storeImageAndGetFeature(resultBuilder, image, false, faceFields);
            return resultBuilder.build();
        } else {
            throw ExceptionSupport.toException(ExceptionEnum.FACE_NOT_MATCH);
        }
    }

    /**
     * 用户组筛选
     * -过滤数据库中无效的用户组
     * -过滤相同组相同用户的向量信息
     */
    private List<FaceSearchVo> filterForValidGroup(Map<Long, String> gidWithGroupIdMaps, List<FaceSearchVo> source) {
        List<FaceSearchVo> dbEffectiveGroupResults = new ArrayList<>();

        for (FaceSearchVo vectorsQueryRes : source) {
            Long gid = vectorsQueryRes.getGid();
            if (gidWithGroupIdMaps.containsKey(gid)) {
                dbEffectiveGroupResults.add(vectorsQueryRes);
            }
        }

        return faceFaceCacheHelper.filterSameGroupSameUser(dbEffectiveGroupResults);
    }

    /**
     * 人脸认证，若查询结果中没有目标用户的id标识符，则抛出异常给客户端
     */
    private void checkVerificationUserInfo(List<FaceSearchVo> source, String userId, UserInfoDO targetUser) {
        if (CollectionUtils.isEmpty(source)) {
            return;
        }
        int unUserCount = 0;
        for (FaceSearchVo item : source) {
            if (targetUser != null && !targetUser.getId().equals(item.getUid())) {
                unUserCount++;
            }
        }
        if (unUserCount == source.size()) {
            if (!StringUtils.isEmpty(userId) && targetUser != null) {
                throw ExceptionSupport.toException(ExceptionEnum.FACE_NOT_MATCH, userId);
            }
        }
    }

    private List<UserInfoDO> getValidUserInfo(Long appId, List<FaceSearchVo> source) {
        List<Long> gidList = new ArrayList<>();
        List<Long> uidList = new ArrayList<>();
        if (CollectionUtils.isEmpty(source)) {
            return null;
        }
        for (FaceSearchVo item : source) {
            gidList.add(item.getGid());
            uidList.add(item.getUid());
        }
        return userInfoMapper.queryBatch(appId, gidList, uidList);
    }

    private UserInfoDO getVerificationUserInfo(Long appId, String userId) {
        UserInfoDO query = new UserInfoDO();
        query.setAppId(appId);
        query.setUserId(userId);
        List<UserInfoDO> result = userInfoMapper.select(query);
        if (result == null) {
            log.info("人脸搜索，人脸认证功能指定用户不存在...");
            throw ExceptionSupport.toException(ExceptionEnum.USER_NOT_FOUND, userId);
        }
        return result.get(0);
    }

    private Map<Long, String> convertToEffectiveGidMaps(Long appId, String groupIdReqParam) {
        List<GroupInfoDO> groupList = groupInfoService.queryBatch(appId, groupIdReqParam);

        int groupEmptyCounters = 0;
        Map<Long, String> effectiveGidMaps = new ConcurrentHashMap<>(groupList.size());
        for (GroupInfoDO item : groupList) {
            if (item.getFaceNumber() == 0) {
                groupEmptyCounters++;
            }
            effectiveGidMaps.put(item.getId(), item.getGroupId());
        }

        //if all group is invalid, the error_msg will be given to client
        if (groupEmptyCounters == groupList.size()) {
            throw ExceptionSupport.toException(ExceptionEnum.EMPTY_GROUP, groupIdReqParam);
        }
        return effectiveGidMaps;
    }

    @Override
    public NLFace.CloudFaceSendMessage compare(FaceSetFaceCompareReqDTO request) {
        Set<String> faceFields = this.checkFaceFieldAndSplitToArray(request.getFaceFields());
        String firstImage = request.getFirstImage();
        String secondImage = request.getSecondImage();

        Integer taskType = mqMessageService.getTaskType(FACE_TASK_TYPE_FEATURE);
        NLFace.CloudFaceSendMessage feature1 = mqMessageService.amqpHelper(firstImage, 1, taskType);
        NLFace.CloudFaceSendMessage feature2 = mqMessageService.amqpHelper(secondImage, 1, taskType);
        List<Float> featuresList1 = feature1.getFeatureResultList().get(0).getFeaturesList();
        List<Float> featuresList2 = feature2.getFeatureResultList().get(0).getFeaturesList();

        float distance = CosineDistanceTool.getNormalDistance(ArrayUtils.toPrimitive(featuresList1.toArray(new Float[0]), 0.0F), ArrayUtils.toPrimitive(featuresList2.toArray(new Float[0]), 0.0F));

        NLFace.CloudFaceSendMessage.Builder result = NLFace.CloudFaceSendMessage.newBuilder();
        result.setConfidence(distance);
        result.setLogId(UUID.randomUUID().toString());

        this.storeImageAndGetFeature(result, firstImage, true, faceFields);
        this.storeImageAndGetFeature(result, secondImage, true, faceFields);
        return result.build();
    }

    /**
     * •检测图片中人脸的多项属性
     * <p>
     * 可选参数 coordinate,liveness
     */
    @Override
    public NLFace.CloudFaceSendMessage multiAttribute(FaceDetectReqDTO vo) {
        Integer taskType = mqMessageService.getTaskType(FACE_TASK_TYPE_MULTIATTRIBUTE);
        return this.storeImageAndGetFeature(vo.getImage(), vo.getMaxFaceNum(), vo.getFaceFields(), taskType);
    }

    /**
     * •检测图片中的人脸是否为活体。此为单目静默式活体检测，即检测图像是否为二次翻拍
     * <p>
     * 可选参数 coordinate
     */
    @Override
    public NLFace.CloudFaceSendMessage liveness(FaceDetectReqDTO vo) {
        Integer taskType = mqMessageService.getTaskType(FACE_TASK_TYPE_LIVENESS);
        return this.storeImageAndGetFeature(vo.getImage(), vo.getMaxFaceNum(), vo.getFaceFields(), taskType);
    }

    /**
     * •检测图片中的人脸并标记出位置信息
     * •展示人脸的五个核心关键点与轮廓 106 个关键点信息
     * <p>
     * 可选参数 liveness
     */
    @Override
    public NLFace.CloudFaceSendMessage detect(FaceDetectReqDTO vo) {
        Integer taskType = mqMessageService.getTaskType(FACE_TASK_TYPE_COORDINATE);
        return this.storeImageAndGetFeature(vo.getImage(), vo.getMaxFaceNum(), vo.getFaceFields(), taskType);
    }


    @Override
    public NLFace.CloudFaceSendMessage features(NLBackend.BackendAllRequest receive, int model) {
        qualityCheckService.checkQuality(receive.getQualityControl(), receive.getImage());
        FaceDO query = ProtobufUtils.parseTo(receive, FaceDO.class);
        FaceDO temp = this.storeImageAndGetMainFeature(query.getImage(), model);
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

    private FaceDO storeImageAndGetMainFeature(String image, int model) {
        this.storeImage(image);

        NLFace.CloudFaceSendMessage feature = mqMessageService.amqpHelper(image, 1, model);

        if (model == -20) {
            NLFace.CloudFaceSendMessage.Builder builder = feature.toBuilder();
            FaceDO faceDO = new FaceDO();
            faceDO.setFeaturesNew(feature.getFeature());
            faceDO.setVersion(feature.getVersion());
            List<Float> preFeature = builder.getFeatureResult(0).getFeaturesList();
            float[] afterFeature = new float[preFeature.size()];
            float tempSum = 0;
            float tempAdd = 0;
            for (Float aFloat : preFeature) {
                tempSum += aFloat * aFloat;
            }
            for (int i = 0; i < preFeature.size(); i++) {
                tempAdd = preFeature.get(i) / (float) Math.sqrt(tempSum);
                afterFeature[i] = tempAdd;
            }
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            ObjectOutputStream outputStream;
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


    /**
     * •存储图片
     * •执行默认任务
     * •执行faceField域可选任务
     * <p>
     * 可选参数 liveness,coordinate
     */
    private NLFace.CloudFaceSendMessage storeImageAndGetFeature(String image, Integer maxFaceNum, String faceFieldTaskTypeKey, Integer defaultTaskTypeKey) {
        Set<String> optionTaskTypeKeys = this.checkFaceFieldAndSplitToArray(faceFieldTaskTypeKey);
        this.storeImage(image);

        NLFace.CloudFaceSendMessage.Builder builder = this.getDefaultFeature(image, maxFaceNum, defaultTaskTypeKey);

        this.getFaceFieldFeature(builder, image, maxFaceNum, optionTaskTypeKeys, defaultTaskTypeKey);

        builder.setLogId(UUID.randomUUID().toString());
        return builder.build();
    }

    /**
     * •存储图片
     * •执行faceField域可选任务
     * <p>
     * 可选参数 liveness,coordinate
     */
    private NLFace.CloudFaceSendMessage storeImageAndGetFeature(NLFace.CloudFaceSendMessage.Builder builder,
                                                                String image, boolean isStoreImage, Set<String> faceFields) throws BaseException {
        if (isStoreImage) {
            this.storeImage(image);
        }

        this.getFaceFieldFeature(builder, image, 1, faceFields, null);

        builder.setLogId(UUID.randomUUID().toString());
        return builder.build();
    }

    private void storeImage(String image) {
        //检查图片
        ImageCheckUtils.imageCheck(image);
        //是否异步存储图片
        try {
            if (enableImageStorage) {
                imageStorageService.asyncUpload(UploadReqDTO.builder().image(image).build());
            } else {
                imageStorageService.uploadV2(UploadReqDTO.builder().image(image).build());
            }
        } catch (IOException exception) {
            throw ExceptionSupport.toException(GlobalExceptionEnum.SYSTEM_ERROR, exception);
        }
    }

    private NLFace.CloudFaceSendMessage.Builder getDefaultFeature(String image, Integer maxFaceNum, Integer defaultTaskTypeKey) {
        NLFace.CloudFaceSendMessage.Builder builder = NLFace.CloudFaceSendMessage.newBuilder();
        if (defaultTaskTypeKey != null) {
            NLFace.CloudFaceSendMessage def =
                    mqMessageService.amqpHelper(image, maxFaceNum, defaultTaskTypeKey);
            builder.mergeFrom(def);
        }
        return builder;
    }

    private void getFaceFieldFeature(NLFace.CloudFaceSendMessage.Builder builder,
                                     String image, Integer maxFaceNum, Set<String> faceFields, Integer defaultTaskTypeKey) {
        if (!CollectionUtils.isEmpty(faceFields)) {
            faceFields.forEach(item -> {
                Integer taskType = mqMessageService.getTaskType(item);
                if (taskType != null && !taskType.equals(defaultTaskTypeKey)) {
                    NLFace.CloudFaceSendMessage msg = mqMessageService.amqpHelper(image, maxFaceNum, taskType);
                    builder.mergeFrom(msg);
                }
            });
        }
    }

    private Set<String> checkFaceFieldAndSplitToArray(String faceField) {
        if (StringUtils.isEmpty(faceField)) {
            return null;
        }
        String[] faceFields = faceField.split(FIELD_SPLIT_REGEX);
        for (String item : faceFields) {
            boolean coordinate = FACE_FIELD_COORDINATE.equals(item);
            boolean liveNess = FACE_FIELD_LIVENESS.equals(item);
            if ((!coordinate) && (!liveNess)) {
                throw ExceptionSupport.toException(ExceptionEnum.WRONG_FACE_FIELD);
            }
        }
        return Arrays.stream(faceFields).collect(Collectors.toSet());
    }

}
