package com.newland.tianyan.face.service.impl;

import com.newland.tianya.commons.base.constants.GlobalExceptionEnum;
import com.newland.tianya.commons.base.exception.BaseException;
import com.newland.tianya.commons.base.model.imagestrore.UploadReqDTO;
import com.newland.tianya.commons.base.model.proto.NLBackend;
import com.newland.tianya.commons.base.model.proto.NLFace;
import com.newland.tianya.commons.base.support.ExceptionSupport;
import com.newland.tianya.commons.base.utils.CosineDistanceTool;
import com.newland.tianya.commons.base.utils.FeaturesTool;
import com.newland.tianya.commons.base.utils.ImageCheckUtils;
import com.newland.tianya.commons.base.utils.LogIdUtils;
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
import lombok.Synchronized;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ArrayUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.io.*;
import java.util.*;
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
        log.info("人脸搜索，开始检查图片有效性");
        Long appId = request.getAppId();
        String groupId = request.getGroupId();
        String userId = request.getUserId();
        int maxUserNum = request.getMaxUserNum();
        String image = ImageCheckUtils.imageCheckAndFormatting(request.getImage());
        Set<String> faceFields = this.checkFaceFieldAndSplitToArray(request.getFaceFields());
        Set<String> splitGroupIdList = this.checkAndSplitGroupIdList(groupId);

        log.info("人脸搜索，输入用户组{},进入用户组及用户有效性筛查", groupId);
        Map<Long, String> gidWithGroupIdMaps = this.loadEffectGroupMaps(appId, splitGroupIdList);
        Map<Long, UserInfoDO> uidWithUserInfoMaps = null;
        if (!StringUtils.isEmpty(userId)) {
            log.info("人脸搜索，当前为人脸认证请求userId:{}", userId);
            uidWithUserInfoMaps = this.loadEffectUserMaps(appId, gidWithGroupIdMaps.keySet(), userId);
        }

        log.info("人脸搜索，用户组筛查通过，请求计算图片特征值");
        NLFace.CloudFaceSendMessage.Builder resultBuilder = NLFace.CloudFaceSendMessage.newBuilder();
        resultBuilder.setLogId(LogIdUtils.traceId());
        Integer taskType = mqMessageService.getTaskType(FACE_TASK_TYPE_FEATURE);
        //one thread to fetch main features
        NLFace.CloudFaceSendMessage feature = mqMessageService.amqpHelper(image, maxUserNum, taskType);
        resultBuilder.setFaceNum(feature.getFaceNum());
        //one thread to fetch operation features
        log.info("人脸搜索-异步请求活体或5~106点坐标");
        this.asynGetOperationFeature(resultBuilder, image, faceFields);
        //one thread to store image
        this.storeImage(image);

        List<Float> featureRaw = FeaturesTool.normalizeConvertToList(feature.getFeatureResultList().get(0).getFeaturesList());
        log.info("人脸搜索，图片特征值已获得，开始向量搜索中...");
        List<FaceSearchVo> vectorResultList = faceFaceCacheHelper.query(appId, featureRaw);

        if (!CollectionUtils.isEmpty(vectorResultList)) {
            log.info("人脸搜索，向量搜索得到结果，开始筛选结果集并封装");
            //过滤数据库中无效的用户组
            int countTopK = 0;
            List<FaceSearchVo> faceList = new ArrayList<>(maxUserNum);
            Set<String> gidUidKeySet = new HashSet<>(vectorResultList.size());
            for (FaceSearchVo item : vectorResultList) {
                if (countTopK >= maxUserNum) {
                    break;
                }
                //剔除非选定用户组的记录
                Long gid = item.getGid();
                if (!gidWithGroupIdMaps.containsKey(gid)) {
                    continue;
                }
                //剔除同组同用户的记录
                String vectorId = item.getVectorId().toString();
                String gidUid = vectorId.substring(0, vectorId.length() - 2);
                if (gidUidKeySet.contains(gidUid)) {
                    continue;
                }
                faceList.add(item);
                gidUidKeySet.add(gidUid);
                countTopK++;
            }

            if (!CollectionUtils.isEmpty(faceList)) {
                if (uidWithUserInfoMaps == null) {
                    Set<Long> uidSetFormQueryFaceList = faceList.stream().map(FaceSearchVo::getUid).collect(Collectors.toSet());
                    uidWithUserInfoMaps = this.loadEffectUserMaps(appId, uidSetFormQueryFaceList);
                }
                for (FaceSearchVo item : faceList) {
                    Long uid = item.getUid();
                    Long gid = item.getGid();
                    if (gidWithGroupIdMaps.containsKey(gid) && uidWithUserInfoMaps.containsKey(uid)) {
                        UserInfoDO userInfoDO = uidWithUserInfoMaps.get(uid);
                        //封装结果至protobuf的result集合中
                        NLFace.CloudFaceSearchResult.Builder builder = resultBuilder.addUserResultBuilder();
                        builder.setGroupId(gidWithGroupIdMaps.get(gid));
                        builder.setUserId(userInfoDO.getUserId());
                        builder.setUserName(userInfoDO.getUserName());
                        builder.setUserInfo(userInfoDO.getUserInfo());
                        //置信度
                        builder.setConfidence(item.getDistance());
                    }
                }

                return resultBuilder.build();
            }
        }
        throw ExceptionSupport.toException(ExceptionEnum.FACE_NOT_MATCH);
    }

    private Set<String> checkAndSplitGroupIdList(String requestGroupIdsStr) {
        Set<String> groupIdSet = new HashSet<>();
        Collections.addAll(groupIdSet, requestGroupIdsStr.split(ID_SPLIT_REGEX));
        if (groupIdSet.size() > SEARCH_MAX_GROUP_NUMBER) {
            throw ExceptionSupport.toException(ExceptionEnum.OVER_GROUP_MAX_NUMBER);
        }

        for (String item : groupIdSet) {
            if (item.length() > MAX_GROUP_LENGTH) {
                throw ExceptionSupport.toException(GlobalExceptionEnum.ARGUMENT_SIZE_MAX, "group_id:" + item);
            }
        }
        return groupIdSet;
    }

    private Map<Long, String> loadEffectGroupMaps(Long appId, Set<String> groupIdList) {
        List<GroupInfoDO> groupList = groupInfoService.queryBatch(appId, groupIdList);

        int groupEmptyCounters = 0;
        Map<Long, String> effectiveGidMaps = new HashMap<>(groupList.size());
        for (GroupInfoDO item : groupList) {
            if (item.getFaceNumber() == 0) {
                groupEmptyCounters++;
            }
            effectiveGidMaps.put(item.getId(), item.getGroupId());

        }

        //if all group is invalid, the error_msg will be given to client
        if (groupEmptyCounters == groupList.size()) {
            throw ExceptionSupport.toException(ExceptionEnum.EMPTY_GROUP, groupIdList.toString());
        }
        return effectiveGidMaps;
    }

    private Map<Long, UserInfoDO> loadEffectUserMaps(Long appId, Set<Long> gidIdSet, String userId) {
        List<UserInfoDO> userInfoList = userInfoMapper.queryBatch(appId, gidIdSet, null, userId);
        if (CollectionUtils.isEmpty(userInfoList)) {
            log.info("人脸搜索，人脸认证功能指定用户不存在...");
            throw ExceptionSupport.toException(ExceptionEnum.USER_NOT_FOUND, userId);
        }
        Map<Long, UserInfoDO> uidWithUserInfoMaps = new HashMap<>(userInfoList.size());
        userInfoList.forEach(userInfoDO -> uidWithUserInfoMaps.putIfAbsent(userInfoDO.getId(), userInfoDO));
        return uidWithUserInfoMaps;
    }

    private Map<Long, UserInfoDO> loadEffectUserMaps(Long appId, Set<Long> uidIdSet) {
        List<UserInfoDO> userInfoList = userInfoMapper.queryBatch(appId, null, uidIdSet, null);
        if (CollectionUtils.isEmpty(userInfoList)) {
            log.info("人脸搜索，查询用户组用户信息为空...");
            throw ExceptionSupport.toException(ExceptionEnum.EMPTY_GROUP);
        }
        Map<Long, UserInfoDO> uidWithUserInfoMaps = new HashMap<>(userInfoList.size());
        userInfoList.forEach(userInfoDO -> uidWithUserInfoMaps.putIfAbsent(userInfoDO.getId(), userInfoDO));
        return uidWithUserInfoMaps;
    }

    @Override
    public NLFace.CloudFaceSendMessage compare(FaceSetFaceCompareReqDTO request) {
        log.info("人脸比对，开始检查参数");
        Set<String> faceFields = this.checkFaceFieldAndSplitToArray(request.getFaceFields());
        String firstImage = ImageCheckUtils.imageCheckAndFormatting(request.getFirstImage());
        String secondImage = ImageCheckUtils.imageCheckAndFormatting(request.getSecondImage());
        log.info("人脸比对，开始请求特征值");
        Integer taskType = mqMessageService.getTaskType(FACE_TASK_TYPE_FEATURE);
        NLFace.CloudFaceSendMessage feature1 = mqMessageService.amqpHelper(firstImage, 1, taskType);
        NLFace.CloudFaceSendMessage feature2 = mqMessageService.amqpHelper(secondImage, 1, taskType);
        List<Float> featuresList1 = feature1.getFeatureResultList().get(0).getFeaturesList();
        List<Float> featuresList2 = feature2.getFeatureResultList().get(0).getFeaturesList();
        log.info("人脸比对，开始计算置信度并封装参数");
        float distance = CosineDistanceTool.getNormalDistance(ArrayUtils.toPrimitive(featuresList1.toArray(new Float[0]), 0.0F), ArrayUtils.toPrimitive(featuresList2.toArray(new Float[0]), 0.0F));

        NLFace.CloudFaceSendMessage.Builder result = NLFace.CloudFaceSendMessage.newBuilder();
        result.setConfidence(distance);
        result.setLogId(UUID.randomUUID().toString());
        log.info("人脸比对，开始异步提交图片");
        this.storeImage(firstImage);
        this.storeImage(secondImage);
        this.getOperationFeature(result, firstImage, faceFields);
        this.getOperationFeature(result, secondImage, faceFields);
        return result.build();
    }

    /**
     * •检测图片中人脸的多项属性
     * <p>
     * 可选参数 coordinate,liveness
     */
    @Override
    public NLFace.CloudFaceSendMessage multiAttribute(FaceDetectReqDTO vo) {
        Set<String> optionTaskTypeKeys = this.checkFaceFieldAndSplitToArray(vo.getFaceFields());
        String image = ImageCheckUtils.imageCheckAndFormatting(vo.getImage());
        this.storeImage(image);
        Integer taskType = mqMessageService.getTaskType(FACE_TASK_TYPE_MULTIATTRIBUTE);
        return this.getOperationFeature(image, vo.getMaxFaceNum(), optionTaskTypeKeys, taskType);
    }

    /**
     * •检测图片中的人脸是否为活体。此为单目静默式活体检测，即检测图像是否为二次翻拍
     * <p>
     * 可选参数 coordinate
     */
    @Override
    public NLFace.CloudFaceSendMessage liveness(FaceDetectReqDTO vo) {
        Set<String> optionTaskTypeKeys = this.checkFaceFieldAndSplitToArray(vo.getFaceFields());
        String image = ImageCheckUtils.imageCheckAndFormatting(vo.getImage());
        this.storeImage(image);
        Integer taskType = mqMessageService.getTaskType(FACE_TASK_TYPE_LIVENESS);
        return this.getOperationFeature(image, vo.getMaxFaceNum(), optionTaskTypeKeys, taskType);
    }

    /**
     * •检测图片中的人脸并标记出位置信息
     * •展示人脸的五个核心关键点与轮廓 106 个关键点信息
     * <p>
     * 可选参数 liveness
     */
    @Override
    public NLFace.CloudFaceSendMessage detect(FaceDetectReqDTO vo) {
        Set<String> optionTaskTypeKeys = this.checkFaceFieldAndSplitToArray(vo.getFaceFields());
        String image = ImageCheckUtils.imageCheckAndFormatting(vo.getImage());
        this.storeImage(image);
        Integer taskType = mqMessageService.getTaskType(FACE_TASK_TYPE_COORDINATE);
        return this.getOperationFeature(image, vo.getMaxFaceNum(), optionTaskTypeKeys, taskType);
    }


    @Override
    public NLFace.CloudFaceSendMessage features(NLBackend.BackendAllRequest receive, int model) {
        String image = ImageCheckUtils.imageCheckAndFormatting(receive.getImage());
        qualityCheckService.checkQuality(receive.getQualityControl(), image);
        this.storeImage(image);
        FaceDO temp = this.getMainFeature(image, model);
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

    private FaceDO getMainFeature(String image, int model) {

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
     * •执行默认任务
     * •执行faceField域可选任务
     * <p>
     * 可选参数 liveness,coordinate
     */
    private NLFace.CloudFaceSendMessage getOperationFeature(String image, Integer maxFaceNum, Set<String> optionTaskTypeKeys, Integer defaultTaskTypeKey) {

        NLFace.CloudFaceSendMessage.Builder builder = this.getDefaultFeature(image, maxFaceNum, defaultTaskTypeKey);

        this.getFaceFieldFeature(builder, image, maxFaceNum, optionTaskTypeKeys, defaultTaskTypeKey);

        builder.setLogId(LogIdUtils.traceId());
        return builder.build();
    }

    /**
     * •执行faceField域可选任务
     * <p>
     * 可选参数 liveness,coordinate
     */
    private void getOperationFeature(NLFace.CloudFaceSendMessage.Builder builder, String image, Set<String> faceFields) throws BaseException {

        this.getFaceFieldFeature(builder, image, 1, faceFields, null);

        builder.setLogId(LogIdUtils.traceId());
        builder.build();
    }

    @Async
    public void asynGetOperationFeature(NLFace.CloudFaceSendMessage.Builder builder, String image, Set<String> faceFields) throws BaseException {
        this.getFaceFieldFeature(builder, image, 1, faceFields, null);
        builder.build();
    }

    @Async
    public void storeImage(String image) {
        //是否异步存储图片
        try {
            if (enableImageStorage) {
                imageStorageService.asyncUpload(UploadReqDTO.builder().image(image).build());
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
