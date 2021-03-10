package com.newland.tianyan.face.service.impl;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.googlecode.protobuf.format.JsonFormat;
import com.newland.tianyan.common.exception.BaseException;
import com.newland.tianyan.common.model.imagestrore.DownloadReqDTO;
import com.newland.tianyan.common.model.imagestrore.UploadReqDTO;
import com.newland.tianyan.common.utils.FeaturesTool;
import com.newland.tianyan.common.utils.JsonUtils;
import com.newland.tianyan.common.utils.ProtobufUtils;
import com.newland.tianyan.common.utils.message.NLBackend;
import com.newland.tianyan.face.constant.BusinessErrorEnums;
import com.newland.tianyan.face.constant.StatusConstants;
import com.newland.tianyan.face.constant.SysErrorEnums;
import com.newland.tianyan.face.dao.FaceMapper;
import com.newland.tianyan.face.dao.GroupInfoMapper;
import com.newland.tianyan.face.dao.UserInfoMapper;
import com.newland.tianyan.face.domain.entity.FaceDO;
import com.newland.tianyan.face.domain.entity.GroupInfoDO;
import com.newland.tianyan.face.domain.entity.UserInfoDO;
import com.newland.tianyan.face.event.face.FaceCreateEvent;
import com.newland.tianyan.face.event.face.FaceDeleteEvent;
import com.newland.tianyan.face.event.group.AbstractGroupCreateEvent;
import com.newland.tianyan.face.event.user.UserCreateEvent;
import com.newland.tianyan.face.feign.client.ImageStoreFeignService;
import com.newland.tianyan.face.mq.RabbitMQSender;
import com.newland.tianyan.face.mq.RabbitMqQueueName;
import com.newland.tianyan.face.service.FacesetUserFaceService;
import lombok.extern.slf4j.Slf4j;
import newlandFace.NLFace;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.io.IOException;
import java.util.*;

import static com.newland.tianyan.face.constant.BusinessArgumentConstants.*;
import static java.lang.Math.abs;

/**
 * @Author: huangJunJie  2020-11-04 09:15
 */
@Service
@Slf4j
public class FacesetUserFaceServiceImpl implements FacesetUserFaceService {

    @Autowired
    private ImageStoreFeignService imageStorageService;
    @Autowired
    private UserInfoMapper userInfoMapper;
    @Autowired
    private FaceCacheHelperImpl<FaceDO> faceCacheHelper;
    @Autowired
    private FaceMapper faceMapper;
    @Autowired
    private GroupInfoMapper groupInfoMapper;
    @Autowired
    private ApplicationEventPublisher publisher;
    @Autowired
    private RabbitMQSender rabbitMqSender;

    @Override
    @Transactional(propagation = Propagation.REQUIRES_NEW, rollbackFor = Exception.class)
    public FaceDO create(NLBackend.BackendAllRequest receive) throws IOException {
        String actionType = receive.getActionType();
        int qualityControl = receive.getQualityControl();
        if (qualityControl != 0) {
            this.handleImageQualityControl(qualityControl, receive.getImage());
        }
        FaceDO query = ProtobufUtils.parseTo(receive, FaceDO.class);
        FaceDO insertFaceDO = new FaceDO();
        //图片提交至服务器
        this.uploadImage(insertFaceDO, receive.getImage());
        //处理特征值
        this.handleFeatures(insertFaceDO, receive.getImage());
        insertFaceDO.setAppId(receive.getAppId());
        insertFaceDO.setUserId(receive.getUserId());

        String[] groups = receive.getGroupId().split(",");
        //去重
        Set<String> groupIdSet = new HashSet<>(Arrays.asList(groups));
        for (String groupId : groupIdSet) {
            query.setGroupId(groupId);
            //如果group_info表中不存在该用户组，则往group_info表中插入一条新记录表示新增了该用户组
            GroupInfoDO groupInfoDO = new GroupInfoDO();
            groupInfoDO.setAppId(query.getAppId());
            groupInfoDO.setGroupId(query.getGroupId());
            groupInfoDO.setIsDelete(StatusConstants.NOT_DELETE);
            groupInfoDO = groupInfoMapper.selectOne(groupInfoDO);
            if (groupInfoDO == null) {
                GroupInfoDO insertGroup = new GroupInfoDO();
                insertGroup.setAppId(query.getAppId());
                insertGroup.setGroupId(query.getGroupId());
                insertGroup.setIsDelete(StatusConstants.NOT_DELETE);
                insertGroup.setUserNumber(0);
                insertGroup.setFaceNumber(0);
                groupInfoMapper.insertGetId(insertGroup);
                publisher.publishEvent(new AbstractGroupCreateEvent(query.getAppId(), query.getGroupId()));
                groupInfoDO = insertGroup;
            }
            insertFaceDO.setGroupId(groupId);

            if (groupInfoDO.getUserNumber() > MAX_USER_NUMBER) {
                throw BusinessErrorEnums.OVER_USE_MAX_NUMBER.toException();
            }
            //如果user_info表中不存在该用户，那么在添加人脸后，往user_info表中插入一条新记录表示新增了该用户。
            UserInfoDO queryUser = new UserInfoDO();
            queryUser.setAppId(query.getAppId());
            queryUser.setGid(groupInfoDO.getId());
            queryUser.setGroupId(groupId);
            queryUser.setUserId(receive.getUserId());
            UserInfoDO sourceUser = userInfoMapper.selectOne(queryUser);
            if (sourceUser == null) {
                //往user_info表中插入一条新记录
                UserInfoDO userInfoDO = new UserInfoDO();
                userInfoDO.setAppId(query.getAppId());
                userInfoDO.setGid(groupInfoDO.getId());
                userInfoDO.setGroupId(query.getGroupId());
                userInfoDO.setUserId(receive.getUserId());
                if (!receive.getUserName().isEmpty()) {
                    userInfoDO.setUserName(receive.getUserName());
                } else {
                    userInfoDO.setUserName(receive.getUserId());
                }
                userInfoDO.setUserInfo(receive.getUserInfo());
                //此时就是在进行添加人脸的操作，所以直接将人脸数的初始值设置为1
                userInfoDO.setFaceNumber(1);
                userInfoMapper.insertGetId(userInfoDO);

                //添加人脸
                insertFaceDO.setUid(userInfoDO.getId());
                insertFaceDO.setGid(groupInfoDO.getId());
                insertFaceDO.setId(MilvusKey.generatedKey(insertFaceDO.getGid(), insertFaceDO.getUid(), userInfoDO.getFaceNumber() + 1));
                //note 缓存中添加用户的人脸
                faceCacheHelper.add(insertFaceDO);
                if (faceMapper.insertSelective(insertFaceDO) <= 0) {
                    throw SysErrorEnums.DB_INSERT_ERROR.toException(JsonUtils.toJson(insertFaceDO));
                }
                //发布事件。由于新增了用户，所以要在group_info表中将该用户对应的那个用户组的记录进行更新（更新的字段是user_number和face_number）
                publisher.publishEvent(new UserCreateEvent(query.getAppId(), query.getGroupId(), query.getUserId(), 1, 1));
            }
            //如果user表中已存在该用户，那么根据action_type的值来进行相应操作。action_type取值有两种("append"和"replace")。
            //下面只实现了当action_type取值为"append"时的执行逻辑，而当action_type取值为"replace"时的执行逻辑这里暂不实现。
            else {
                insertFaceDO.setUid(sourceUser.getId());
                insertFaceDO.setGid(sourceUser.getGid());
                if ("append".equals(actionType)) {
                    //note 缓存中添加用户的人脸
                    insertFaceDO.setId(MilvusKey.generatedKey(insertFaceDO.getGid(), insertFaceDO.getUid(), sourceUser.getFaceNumber() + 1));
                    faceCacheHelper.add(insertFaceDO);
                    //添加人脸
                    if (faceMapper.insertSelective(insertFaceDO) <= 0) {
                        throw SysErrorEnums.DB_INSERT_ERROR.toException(JsonUtils.toJson(insertFaceDO));
                    }
                    //发布事件。已存在的用户添加了人脸，所以要在user_info中将该用户对应的那条记录进行更新（更新的字段是face_number）,也要在group_info表中将该用户对应的那个用户组的记录进行更新（更新的字段同样是face_number）
                    publisher.publishEvent(new FaceCreateEvent(query.getAppId(), query.getGroupId(), query.getUserId()));
                } else if ("replace".equals(actionType)) {
                    insertFaceDO.setId(MilvusKey.generatedKey(insertFaceDO.getGid(), insertFaceDO.getUid(), 1));
                    //删除face表中该用户原本的人脸（用户在一个组中可能有多张人脸）
                    FaceDO faceDO = new FaceDO();
                    faceDO.setGroupId(groupId);
                    faceDO.setUserId(query.getUserId());
                    faceDO.setAppId(query.getAppId());
                    List<Long> faceIdList = faceMapper.selectIdByGroupId(groupId);
                    //note 删除原本缓存中的人脸，添加新的人脸
                    if ((!CollectionUtils.isEmpty(faceIdList))) {
                        faceCacheHelper.deleteBatch(query.getAppId(), faceIdList);
                    }
                    int deleteCount;
                    deleteCount = faceMapper.delete(faceDO);
                    if (deleteCount < 0) {
                        throw SysErrorEnums.DB_DELETE_ERROR.toException(JsonUtils.toJson(faceDO));
                    }
                    //添加该用户新的人脸（只有一张）
                    faceCacheHelper.add(insertFaceDO);
                    if (faceMapper.insertSelective(insertFaceDO) <= 0) {
                        throw SysErrorEnums.DB_INSERT_ERROR.toException(JsonUtils.toJson(insertFaceDO));
                    }
                    //人脸替换后更新user_info表中该用户的face_number,也要更新group_info表中该用户对在的用户组对应的那条记录中的face_number
                    userInfoMapper.faceNumberIncrease(receive.getAppId(), groupId, receive.getUserId(), 1 - deleteCount);
                    groupInfoMapper.faceNumberIncrease(receive.getAppId(), groupId, 1 - deleteCount);
                } else {
                    throw BusinessErrorEnums.USER_NOT_FOUND.toException(receive.getUserId());
                }
            }
        }
        return insertFaceDO;
    }

    private void handleImageQualityControl(int qualityControl, String image) {
        NLFace.CloudFaceSendMessage.Builder detectBuilder = NLFace.CloudFaceSendMessage.newBuilder();
        NLFace.CloudFaceSendMessage detectDef = amqpHelper(image, 1, 1);
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
                amqpHelper(image, 1, 4);
        builder.mergeFrom(def);
        NLFace.CloudFaceSendMessage qualityRe = builder.build();
        if (qualityControl == 1) {
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
        if (qualityControl == 2) {
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
        if (qualityControl == 3) {
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


    public NLFace.CloudFaceSendMessage amqpHelper(String fileName, int maxFaceNum, Integer taskType) throws BaseException {
        //封装请求
        NLFace.CloudFaceAllRequest.Builder amqpRequest = NLFace.CloudFaceAllRequest.newBuilder();
        amqpRequest.setLogId(UUID.randomUUID().toString());
        amqpRequest.setTaskType(taskType);
        amqpRequest.setImage(fileName);
        amqpRequest.setMaxFaceNum(maxFaceNum);
        byte[] message = amqpRequest.build().toByteArray();
        //请求MQ
        String json = new String(rabbitMqSender.send(RabbitMqQueueName.FACE_DETECT_QUEUE, message));
        //处理结果
        NLFace.CloudFaceSendMessage.Builder result = NLFace.CloudFaceSendMessage.newBuilder();
        try {
            JsonFormat.merge(json, result);
        } catch (JsonFormat.ParseException e) {
            throw SysErrorEnums.PROTO_PARSE_ERROR.toException();
        }
        return result.build();
    }

    private void uploadImage(FaceDO faceDO, String image) throws IOException {
        //提交至指定服务器路径
        UploadReqDTO uploadReq = UploadReqDTO.builder().image(image).build();
        String imagePath = imageStorageService.uploadV2(uploadReq).getImagePath();
        faceDO.setImagePath(imagePath);
    }

    private void handleFeatures(FaceDO faceDO, String image) {
        NLFace.CloudFaceSendMessage feature =
                amqpHelper(image, 1, 2);
        NLFace.CloudFaceSendMessage.Builder builder = feature.toBuilder();
        List<Float> featureList = builder.getFeatureResult(0).getFeaturesList();
        faceDO.setFeatures(FeaturesTool.normalizeConvertToByte(featureList));
    }

    @Override
    public List<FaceDO> getList(NLBackend.BackendAllRequest receive) {
        FaceDO query = ProtobufUtils.parseTo(receive, FaceDO.class);
        if (!StringUtils.isEmpty(query.getGroupId())) {
            GroupInfoDO groupInfoDO = new GroupInfoDO();
            groupInfoDO.setAppId(query.getAppId());
            groupInfoDO.setGroupId(query.getGroupId());
            groupInfoDO.setIsDelete(StatusConstants.NOT_DELETE);
            if (groupInfoMapper.selectCount(groupInfoDO) <= 0) {
                return new ArrayList<>();
            }
        }

        PageInfo<FaceDO> facePageInfo = PageHelper.offsetPage(query.getStartIndex(), query.getLength())
                .setOrderBy("create_time desc")
                .doSelectPageInfo(
                        () -> faceMapper.select(query));

        List<FaceDO> face = facePageInfo.getList();
        if (CollectionUtils.isEmpty(face)) {
            return face;
        }
        for (FaceDO faceDO : face) {
            if (faceDO.getImagePath() != null) {
                DownloadReqDTO downloadReq = DownloadReqDTO.builder().imagePath(faceDO.getImagePath()).build();
                faceDO.setImage(imageStorageService.download(downloadReq).getImage());
            }
            faceDO.setFaceId(faceDO.getId().toString());
        }
        return face;
    }

    /**
     * 删除人脸
     */
    @Override
    public void delete(NLBackend.BackendAllRequest receive) {
        FaceDO query = ProtobufUtils.parseTo(receive, FaceDO.class);
        query.setId(Long.parseLong(query.getFaceId()));

        //检查group_info表中是否存在该group_id
        if (!StringUtils.isEmpty(receive.getGroupId())) {
            GroupInfoDO groupInfoDO = new GroupInfoDO();
            groupInfoDO.setAppId(groupInfoDO.getAppId());
            groupInfoDO.setGroupId(groupInfoDO.getGroupId());
            groupInfoDO.setIsDelete(StatusConstants.NOT_DELETE);
            if (groupInfoMapper.selectCount(groupInfoDO) <= 0) {
                throw BusinessErrorEnums.GROUP_NOT_FOUND.toException(query.getGroupId());
            }
        }

        //检查user_info表中是否存在该user_id
        UserInfoDO userInfoDO = new UserInfoDO();
        userInfoDO.setAppId(receive.getAppId());
        userInfoDO.setGroupId(receive.getGroupId());
        userInfoDO.setUserId(receive.getUserId());
        if (userInfoMapper.selectCount(userInfoDO) <= 0) {
            throw BusinessErrorEnums.USER_NOT_FOUND.toException(query.getUserId());
        }

        //然后直接去face表中查询是否存在这张人脸图片的记录，若不存在则抛出异常，存在则删除该人脸
        FaceDO faceDO = faceMapper.selectOne(query);
        if (faceDO == null) {
            throw BusinessErrorEnums.FACE_NOT_FOUND.toException();
        }

        //缓存中删除用户指定的人脸
        faceCacheHelper.delete(query.getAppId(), faceDO.getId());
        //物理删除人脸
        if (faceMapper.delete(query) < 0) {
            throw SysErrorEnums.DB_DELETE_ERROR.toException(JsonUtils.toJson(query));
        }
        publisher.publishEvent(new FaceDeleteEvent(query.getAppId(), query.getGroupId(), query.getUserId()));
    }
}
