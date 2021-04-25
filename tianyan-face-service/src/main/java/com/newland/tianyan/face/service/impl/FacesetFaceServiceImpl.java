package com.newland.tianyan.face.service.impl;

import com.newland.tianya.commons.base.constants.GlobalExceptionEnum;
import com.newland.tianya.commons.base.exception.BaseException;
import com.newland.tianya.commons.base.model.proto.NLBackend;
import com.newland.tianya.commons.base.model.proto.NLFace;
import com.newland.tianya.commons.base.support.ExceptionSupport;
import com.newland.tianya.commons.base.utils.CosineDistanceTool;
import com.newland.tianya.commons.base.utils.FeaturesTool;
import com.newland.tianya.commons.base.utils.ImageCheckUtils;
import com.newland.tianya.commons.base.utils.LogIdUtils;
import com.newland.tianyan.face.constant.ExceptionEnum;
import com.newland.tianyan.face.dao.UserInfoMapper;
import com.newland.tianyan.face.domain.dto.*;
import com.newland.tianyan.face.domain.entity.FaceDO;
import com.newland.tianyan.face.domain.entity.GroupInfoDO;
import com.newland.tianyan.face.domain.entity.UserInfoDO;
import com.newland.tianyan.face.domain.vo.FaceSearchVo;
import com.newland.tianyan.face.mq.IMqMessageService;
import com.newland.tianyan.face.service.*;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ArrayUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
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
    private ImageStoreService imageStoreService;
    @Autowired
    private IQualityCheckService qualityCheckService;
    @Autowired
    private IMqMessageService mqMessageService;
    @Value("${enable-image-storage}")
    private boolean enableImageStorage;

    private final static Map<String, Integer> TASK_TYPE_MAPS = new HashMap<String, Integer>() {{
        put(FACE_TASK_TYPE_COORDINATE, 1);
        put(FACE_TASK_TYPE_FEATURE, 2);
        put(FACE_TASK_TYPE_LIVENESS, 3);
        put(FACE_TASK_TYPE_MULTIATTRIBUTE, 4);
    }};

    private Integer getTaskType(String taskTypeKey) {
        return TASK_TYPE_MAPS.getOrDefault(taskTypeKey, null);
    }

    @Override
    public NLFace.CloudFaceSendMessage searchNew(FaceSetFaceSearchReqDTO request) throws BaseException {
        log.info("人脸搜索，开始检查图片有效性");
        Long appId = request.getAppId();
        String groupId = request.getGroupId();
        String userId = request.getUserId();
        int maxUserNum = request.getMaxUserNum();
        String image = ImageCheckUtils.imageCheckAndFormatting(request.getImage());
        Set<String> faceFields = this.splitToArray(request.getFaceFields());
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
        Integer taskType = this.getTaskType(FACE_TASK_TYPE_FEATURE);
        //one thread to fetch main features
        NLFace.CloudFaceSendMessage feature = mqMessageService.amqpHelper(image, maxUserNum, taskType);
        resultBuilder.setFaceNum(feature.getFaceNum());
        //one thread to store image
        this.storeImage(image);

        List<Float> featureRaw = FeaturesTool.normalizeConvertToList(feature.getFeatureResultList().get(0).getFeaturesList());
        log.info("人脸搜索，图片特征值已获得，开始向量搜索中...");
        List<FaceSearchVo> vectorResultList = faceFaceCacheHelper.query(appId, featureRaw);

        if (!CollectionUtils.isEmpty(vectorResultList)) {
            log.info("人脸搜索，向量搜索得到结果，开始筛选结果集并封装");
            int countTopK = 0;
            List<FaceSearchVo> faceList = new ArrayList<>(maxUserNum);
            Set<String> gidUidKeySet = new HashSet<>(vectorResultList.size());
            //留存有效用户组结果
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
            //留存有效用户结果
            if (!CollectionUtils.isEmpty(faceList)) {
                if (uidWithUserInfoMaps == null) {
                    Set<Long> uidSetFormQueryFaceList = faceList.stream().map(FaceSearchVo::getUid).collect(Collectors.toSet());
                    uidWithUserInfoMaps = this.loadEffectUserMaps(appId, uidSetFormQueryFaceList);
                }
            }
            if (gidWithGroupIdMaps.size() > 0 && uidWithUserInfoMaps != null && uidWithUserInfoMaps.size() > 0) {
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
                log.info("人脸搜索-异步请求活体或5~106点坐标");
                this.addOperationFeature(resultBuilder, image, request.getMaxFaceNum(), faceFields);
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

        int groupEmptyFaceCounters = 0;
        int groupEmptyUserCounters = 0;
        Map<Long, String> effectiveGidMaps = new HashMap<>(groupList.size());
        for (GroupInfoDO item : groupList) {
            if (item.getFaceNumber() == 0) {
                groupEmptyFaceCounters++;
            }
            if (item.getUserNumber() == 0) {
                groupEmptyUserCounters++;
            }
            effectiveGidMaps.put(item.getId(), item.getGroupId());
        }

        //if all group is invalid, the error_msg will be given to client
        if (groupEmptyUserCounters == groupList.size()) {
            throw ExceptionSupport.toException(ExceptionEnum.EMPTY_USER_GROUP, groupIdList.toString());
        }
        if (groupEmptyFaceCounters == groupList.size()) {
            throw ExceptionSupport.toException(ExceptionEnum.EMPTY_FACE_GROUP, groupIdList.toString());
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
            throw ExceptionSupport.toException(ExceptionEnum.USER_NOT_FOUND);
        }
        Map<Long, UserInfoDO> uidWithUserInfoMaps = new HashMap<>(userInfoList.size());
        userInfoList.forEach(userInfoDO -> uidWithUserInfoMaps.putIfAbsent(userInfoDO.getId(), userInfoDO));
        return uidWithUserInfoMaps;
    }

    @Override
    public NLFace.CloudFaceSendMessage compare(FaceSetFaceCompareReqDTO request) {
        log.info("人脸比对，开始检查参数");
        Set<String> faceFields = this.splitToArray(request.getFaceFields());
        String firstImage = ImageCheckUtils.imageCheckAndFormatting(request.getFirstImage());
        String secondImage = ImageCheckUtils.imageCheckAndFormatting(request.getSecondImage());
        log.info("人脸比对，开始请求特征值");
        Integer taskType = this.getTaskType(FACE_TASK_TYPE_FEATURE);
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
        this.addOperationFeature(result, firstImage, 1, faceFields);
        this.addOperationFeature(result, secondImage, 1, faceFields);
        return result.build();
    }

    /**
     * •检测图片中人脸的多项属性
     * <p>
     * 可选参数 coordinate,liveness
     */
    @Override
    public NLFace.CloudFaceSendMessage multiAttribute(FaceMultiAttributeReqDTO vo) {
        Set<String> optionTaskTypeKeys = this.splitToArray(vo.getFaceFields());
        String image = ImageCheckUtils.imageCheckAndFormatting(vo.getImage());
        this.storeImage(image);
        Integer taskType = this.getTaskType(FACE_TASK_TYPE_MULTIATTRIBUTE);
        return this.getOperationFeature(image, vo.getMaxFaceNum(), taskType, optionTaskTypeKeys);
    }

    /**
     * •检测图片中的人脸是否为活体。此为单目静默式活体检测，即检测图像是否为二次翻拍
     * <p>
     * 可选参数 coordinate
     */
    @Override
    public NLFace.CloudFaceSendMessage liveness(FaceLiveNessReqDTO vo) {
        Set<String> optionTaskTypeKeys = this.splitToArray(vo.getFaceFields());
        String image = ImageCheckUtils.imageCheckAndFormatting(vo.getImage());
        this.storeImage(image);
        Integer taskType = this.getTaskType(FACE_TASK_TYPE_LIVENESS);
        return this.getOperationFeature(image, vo.getMaxFaceNum(), taskType, optionTaskTypeKeys);
    }

    /**
     * •检测图片中的人脸并标记出位置信息
     * •展示人脸的五个核心关键点与轮廓 106 个关键点信息
     * <p>
     * 可选参数 liveness
     */
    @Override
    public NLFace.CloudFaceSendMessage detect(FaceDetectReqDTO vo) {
        Set<String> optionTaskTypeKeys = this.splitToArray(vo.getFaceFields());
        String image = ImageCheckUtils.imageCheckAndFormatting(vo.getImage());
        this.storeImage(image);
        Integer taskType = this.getTaskType(FACE_TASK_TYPE_COORDINATE);
        return this.getOperationFeature(image, vo.getMaxFaceNum(), taskType, optionTaskTypeKeys);
    }


    @Override
    public NLFace.CloudFaceSendMessage features(NLBackend.BackendAllRequest receive, int model) {
        String image = ImageCheckUtils.imageCheckAndFormatting(receive.getImage());
        qualityCheckService.checkQuality(receive.getQualityControl(), image);
        this.storeImage(image);
        //getFeatures by mq
        NLFace.CloudFaceSendMessage feature = mqMessageService.amqpHelper(image, 1, model);
        //convert response
        NLFace.CloudFaceSendMessage.Builder result = NLFace.CloudFaceSendMessage.newBuilder();
        result.setLogId(LogIdUtils.traceId());
        result.setFaceId(result.getLogId());
        result.setVersion(feature.getVersion());
        int old20 = -20;
        if (model == old20) {
            NLFace.CloudFaceSendMessage.Builder builder = feature.toBuilder();
            List<Float> preFeature = builder.getFeatureResult(0).getFeaturesList();
            byte[] afterFeature = FeaturesTool.normalizeConvertToByte(preFeature);

            ObjectInputStream in;
            float[] features = new float[FeaturesTool.SIZE];
            try {
                in = new ObjectInputStream(new ByteArrayInputStream(afterFeature));
                features = (float[]) in.readObject();
            } catch (IOException | ClassNotFoundException e) {
                e.printStackTrace();
            }
            NLFace.CloudFaceFeatureResult.Builder featureBuilder = result.addFeatureResultBuilder();
            for (int i = 0; i < FeaturesTool.SIZE; i++) {
                featureBuilder.addFeatures(features[i]);
            }

        } else {
            result.setFeature(feature.getFeature());
        }

        return result.build();
    }

    /**
     * •执行默认任务
     * •执行faceField域可选任务
     * <p>
     * 可选参数 liveness,coordinate
     */
    private NLFace.CloudFaceSendMessage getOperationFeature(String image, Integer maxFaceNum, Integer defaultTaskTypeKey, Set<String> optionTaskTypeKeys) {
        NLFace.CloudFaceSendMessage.Builder builder = NLFace.CloudFaceSendMessage.newBuilder();
        builder.setLogId(LogIdUtils.traceId());
        if (defaultTaskTypeKey != null) {
            NLFace.CloudFaceSendMessage def =
                    mqMessageService.amqpHelper(image, maxFaceNum, defaultTaskTypeKey);
            builder.mergeFrom(def);
        }

        if (!CollectionUtils.isEmpty(optionTaskTypeKeys)) {
            optionTaskTypeKeys.forEach(item -> {
                Integer taskType = this.getTaskType(item);
                if (taskType != null && !taskType.equals(defaultTaskTypeKey)) {
                    NLFace.CloudFaceSendMessage msg = mqMessageService.amqpHelper(image, maxFaceNum, taskType);
                    builder.mergeFrom(msg);
                }
            });
        }
        return builder.build();
    }

    /**
     * •执行faceField域可选任务
     * <p>
     * 可选参数 liveness,coordinate
     */
    private void addOperationFeature(NLFace.CloudFaceSendMessage.Builder builder, String image, Integer maxFaceNum, Set<String> optionTaskTypeKeys) throws BaseException {
        builder.setLogId(LogIdUtils.traceId());
        if (!CollectionUtils.isEmpty(optionTaskTypeKeys)) {
            optionTaskTypeKeys.forEach(item -> {
                Integer taskType = this.getTaskType(item);
                if (taskType != null) {
                    NLFace.CloudFaceSendMessage msg = mqMessageService.amqpHelper(image, maxFaceNum, taskType);
                    builder.mergeFrom(msg);
                }
            });
        }
        builder.build();
    }

    public void storeImage(String image) {
        //是否异步存储图片
        if (enableImageStorage) {
            try {
                imageStoreService.uploadAsync(image);
            } catch (IOException exception) {

            }
        }
    }

    private Set<String> splitToArray(String faceField) {
        return StringUtils.isEmpty(faceField) ? new HashSet<>() : new HashSet<>(Arrays.asList(faceField.split(FIELD_SPLIT_REGEX)));
    }

}
