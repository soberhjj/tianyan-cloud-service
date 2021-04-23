package com.newland.tianyan.face.service.impl;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.newland.tianya.commons.base.exception.BusinessException;
import com.newland.tianya.commons.base.model.proto.NLBackend;
import com.newland.tianya.commons.base.model.proto.NLFace;
import com.newland.tianya.commons.base.support.ExceptionSupport;
import com.newland.tianya.commons.base.utils.FeaturesTool;
import com.newland.tianya.commons.base.utils.ImageCheckUtils;
import com.newland.tianya.commons.base.utils.JsonUtils;
import com.newland.tianya.commons.base.utils.ProtobufUtils;
import com.newland.tianyan.face.constant.EntityStatusConstants;
import com.newland.tianyan.face.constant.ExceptionEnum;
import com.newland.tianyan.face.dao.FaceMapper;
import com.newland.tianyan.face.dao.GroupInfoMapper;
import com.newland.tianyan.face.dao.UserInfoMapper;
import com.newland.tianyan.face.domain.entity.FaceDO;
import com.newland.tianyan.face.domain.entity.GroupInfoDO;
import com.newland.tianyan.face.domain.entity.UserInfoDO;
import com.newland.tianyan.face.event.face.FaceCreateEvent;
import com.newland.tianyan.face.event.face.FaceDeleteEvent;
import com.newland.tianyan.face.event.group.GroupCreateEvent;
import com.newland.tianyan.face.event.user.UserCreateEvent;
import com.newland.tianyan.face.mq.IMqMessageService;
import com.newland.tianyan.face.service.FacesetUserFaceService;
import com.newland.tianyan.face.service.IQualityCheckService;
import com.newland.tianyan.face.service.IVectorSearchService;
import com.newland.tianyan.face.service.ImageStoreService;
import com.newland.tianyan.face.utils.FaceIdSlotHelper;
import com.newland.tianyan.face.utils.VectorSearchKeyUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static com.newland.tianyan.face.constant.BusinessArgumentConstants.*;

/**
 * @Author: huangJunJie  2020-11-04 09:15
 */
@Service
@Slf4j
public class FacesetUserFaceServiceImpl implements FacesetUserFaceService {

    @Autowired
    private ImageStoreService imageStorageService;
    @Autowired
    private UserInfoMapper userInfoMapper;
    @Autowired
    private IVectorSearchService<FaceDO> faceCacheHelper;
    @Autowired
    private FaceMapper faceMapper;
    @Autowired
    private GroupInfoMapper groupInfoMapper;
    @Autowired
    private ApplicationEventPublisher publisher;
    @Autowired
    private IMqMessageService iMqMessageService;
    @Autowired
    private IQualityCheckService qualityCheckService;

    @Override
    @Transactional(propagation = Propagation.REQUIRES_NEW, rollbackFor = Exception.class)
    public FaceDO create(NLBackend.BackendAllRequest receive) throws IOException {
        //参数验证
        String image = ImageCheckUtils.imageCheckAndFormatting(receive.getImage());
        String actionType = receive.getActionType();
        this.checkOperationType(actionType);
        qualityCheckService.checkQuality(receive.getQualityControl(), image);

        FaceDO insertFaceDO = new FaceDO();
        log.info("人脸添加-提交图片至存储服务");
        insertFaceDO.setImagePath(imageStorageService.upload(image));
        log.info("人脸添加-请求图片特征值");
        this.handleFeatures(insertFaceDO, image);
        insertFaceDO.setAppId(receive.getAppId());

        Long appId = receive.getAppId();
        String groupId = receive.getGroupId();
        String userId = receive.getUserId();
        //对于不存在的用户组进行新建
        GroupInfoDO groupInfoDO = this.getExistedGroup(appId, groupId);
        insertFaceDO.setGid(groupInfoDO.getId());
        insertFaceDO.setGroupId(groupInfoDO.getGroupId());
        //对于不存在的用户进行新建
        UserInfoDO queryUser = ProtobufUtils.parseTo(receive, UserInfoDO.class);
        queryUser.setGid(groupInfoDO.getId());
        queryUser.setUserInfo(receive.getUserInfo());
        UserInfoDO userInfoDO = this.getExistedUser(queryUser);
        if (userInfoDO.getFaceNumber() > MAX_FACE_NUMBER) {
            throw new BusinessException(ExceptionEnum.OVER_FACE_MAX_NUMBER.getErrorCode(),
                    ExceptionEnum.OVER_FACE_MAX_NUMBER.getErrorMsg());
        }
        insertFaceDO.setUid(userInfoDO.getId());
        insertFaceDO.setUserId(userInfoDO.getUserId());
        FaceIdSlotHelper faceIdSlotHelper = new FaceIdSlotHelper(userInfoDO.getFaceIdSlot());
        insertFaceDO.setId(VectorSearchKeyUtils.generatedKey(insertFaceDO.getGid(), insertFaceDO.getUid(), faceIdSlotHelper.pollNextValidId()));

        //初次录入的情况，添加人脸
        if (userInfoDO.getFaceNumber() == 0) {
            log.info("人脸添加-新增人脸append");
            faceMapper.insertSelective(insertFaceDO);
            publisher.publishEvent(new UserCreateEvent(appId, groupId, userId));
            publisher.publishEvent(new FaceCreateEvent(appId, groupId, userId,
                    userInfoDO.getFaceIdSlot(), insertFaceDO.getId()));
            faceCacheHelper.add(insertFaceDO);
        } else {

            if (ACTION_TYPE_APPEND.equals(actionType)) {
                log.info("人脸添加-追加人脸append");
                faceMapper.insertSelective(insertFaceDO);
                publisher.publishEvent(new FaceCreateEvent(appId, groupId, userId,
                        userInfoDO.getFaceIdSlot(), insertFaceDO.getId()));
                faceCacheHelper.add(insertFaceDO);
            } else if (ACTION_TYPE_REPLACE.equals(actionType)) {
                log.info("人脸添加-清空并添加新的人脸replace");
                //清空人脸
                FaceDO faceDO = new FaceDO();
                faceDO.setGroupId(groupId);
                faceDO.setUserId(userId);
                faceDO.setAppId(appId);
                List<Long> faceIdList = faceMapper.selectIdByGroupId(groupId);
                if ((CollectionUtils.isEmpty(faceIdList))) {
                    return null;
                }
                faceCacheHelper.deleteBatch(appId, faceIdList);
                faceMapper.delete(faceDO);

                faceMapper.insertSelective(insertFaceDO);
                publisher.publishEvent(new FaceDeleteEvent(appId, groupId, userId,
                        faceIdList.size() - 1, userInfoDO.getFaceIdSlot(), faceIdList));
                faceCacheHelper.add(insertFaceDO);
            }
        }

        return insertFaceDO;
    }

    private void checkOperationType(String operationType) {
        if (StringUtils.isEmpty(operationType)) {
            return;
        }
        String[] arr = operationType.split(",");
        for (String item : arr) {
            boolean append = ACTION_TYPE_APPEND.equals(item);
            boolean replace = ACTION_TYPE_REPLACE.equals(item);
            if ((!append) && (!replace)) {
                throw ExceptionSupport.toException(ExceptionEnum.WRONG_ACTION_TYPE);
            }
        }
    }

    private GroupInfoDO getExistedGroup(Long appId, String groupId) {
        GroupInfoDO groupInfoDO = new GroupInfoDO();
        groupInfoDO.setAppId(appId);
        groupInfoDO.setGroupId(groupId);
        groupInfoDO.setIsDelete(EntityStatusConstants.NOT_DELETE);
        groupInfoDO = groupInfoMapper.selectOne(groupInfoDO);
        if (groupInfoDO == null) {
            log.info("人脸添加-目标用户组不存在{}", appId + "/" + groupId);
            GroupInfoDO insertGroup = new GroupInfoDO();
            insertGroup.setAppId(appId);
            insertGroup.setGroupId(groupId);
            insertGroup.setIsDelete(EntityStatusConstants.NOT_DELETE);
            insertGroup.setUserNumber(0);
            insertGroup.setFaceNumber(0);
            groupInfoMapper.insertGetId(insertGroup);
            publisher.publishEvent(new GroupCreateEvent(appId, groupId));
            log.info("人脸添加-新建用户组成功{}", JsonUtils.toJson(insertGroup));
            groupInfoDO = insertGroup;
        }
        if (groupInfoDO.getUserNumber() > MAX_USER_NUMBER) {
            throw ExceptionSupport.toException(ExceptionEnum.OVER_USE_MAX_NUMBER);
        }
        return groupInfoDO;
    }

    private UserInfoDO getExistedUser(UserInfoDO sourceUser) {
        UserInfoDO queryCondition = sourceUser;
        sourceUser = userInfoMapper.selectOne(queryCondition);
        if (sourceUser == null) {
            log.info("人脸添加-目标用户不存在{}", JsonUtils.toJson(queryCondition));
            UserInfoDO userInfoDO = new UserInfoDO();
            userInfoDO.setAppId(queryCondition.getAppId());
            userInfoDO.setGid(queryCondition.getGid());
            userInfoDO.setGroupId(queryCondition.getGroupId());
            userInfoDO.setUserId(queryCondition.getUserId());
            userInfoDO.setUserInfo(queryCondition.getUserInfo());
            userInfoDO.setFaceNumber(0);
            if (!StringUtils.isEmpty(queryCondition.getUserName())) {
                userInfoDO.setUserName(queryCondition.getUserName());
            } else {
                userInfoDO.setUserName(queryCondition.getUserId());
            }
            userInfoMapper.insertGetId(userInfoDO);
            log.info("人脸添加-新建用户成功{}", JsonUtils.toJson(userInfoDO));
            sourceUser = userInfoDO;
        }
        return sourceUser;
    }

    private void handleFeatures(FaceDO faceDO, String image) {
        NLFace.CloudFaceSendMessage feature =
                iMqMessageService.amqpHelper(image, 1, 2);
        NLFace.CloudFaceSendMessage.Builder builder = feature.toBuilder();
        List<Float> featureList = builder.getFeatureResult(0).getFeaturesList();
        faceDO.setFeatures(FeaturesTool.normalizeConvertToByte(featureList));
    }

    @Override
    public List<FaceDO> getList(NLBackend.BackendAllRequest receive) {
        FaceDO query = ProtobufUtils.parseTo(receive, FaceDO.class);
        GroupInfoDO groupInfoDO = new GroupInfoDO();
        groupInfoDO.setAppId(query.getAppId());
        groupInfoDO.setGroupId(query.getGroupId());
        groupInfoDO.setIsDelete(EntityStatusConstants.NOT_DELETE);
        if (groupInfoMapper.selectCount(groupInfoDO) <= 0) {
            throw ExceptionSupport.toException(ExceptionEnum.GROUP_NOT_FOUND,receive.getGroupId());
        }

        PageInfo<FaceDO> facePageInfo = PageHelper.startPage(query.getStartIndex(), query.getLength())
                .setOrderBy("create_time desc")
                .doSelectPageInfo(
                        () -> faceMapper.select(query));

        List<FaceDO> face = facePageInfo.getList();
        if (CollectionUtils.isEmpty(face)) {
            throw ExceptionSupport.toException(ExceptionEnum.USER_NOT_FOUND,receive.getUserId());
        }
        for (FaceDO faceDO : face) {
            if (faceDO.getImagePath() != null) {
                faceDO.setImage(imageStorageService.download(faceDO.getImagePath()));
            }
            faceDO.setFaceId(faceDO.getId().toString());
        }
        return face;
    }

    /**
     * 删除人脸
     */
    @Override
    @Transactional(propagation = Propagation.REQUIRES_NEW, rollbackFor = Exception.class)
    public void delete(NLBackend.BackendAllRequest receive) {
        FaceDO query = ProtobufUtils.parseTo(receive, FaceDO.class);
        query.setId(Long.parseLong(query.getFaceId()));

        //检查group_info表中是否存在该group_id
        if (!StringUtils.isEmpty(receive.getGroupId())) {
            GroupInfoDO groupInfoDO = new GroupInfoDO();
            groupInfoDO.setAppId(groupInfoDO.getAppId());
            groupInfoDO.setGroupId(groupInfoDO.getGroupId());
            groupInfoDO.setIsDelete(EntityStatusConstants.NOT_DELETE);
            if (groupInfoMapper.selectCount(groupInfoDO) <= 0) {
                throw ExceptionSupport.toException(ExceptionEnum.GROUP_NOT_FOUND, query.getGroupId());
            }
        }

        //检查user_info表中是否存在该user_id
        UserInfoDO userInfoDO = new UserInfoDO();
        userInfoDO.setAppId(receive.getAppId());
        userInfoDO.setGroupId(receive.getGroupId());
        userInfoDO.setUserId(receive.getUserId());
        userInfoDO = userInfoMapper.selectOne(userInfoDO);
        if (userInfoDO == null) {
            throw ExceptionSupport.toException(ExceptionEnum.USER_NOT_FOUND, query.getUserId());
        }

        //然后直接去face表中查询是否存在这张人脸图片的记录，若不存在则抛出异常，存在则删除该人脸
        FaceDO faceDO = faceMapper.selectOne(query);
        if (faceDO == null) {
            throw ExceptionSupport.toException(ExceptionEnum.FACE_NOT_FOUND);
        }

        //缓存中删除用户指定的人脸
        faceCacheHelper.delete(query.getAppId(), faceDO.getId());
        //物理删除人脸
        faceMapper.delete(query);

        publisher.publishEvent(new FaceDeleteEvent(userInfoDO.getAppId(), userInfoDO.getGroupId(), userInfoDO.getUserId(),
                1, userInfoDO.getFaceIdSlot(), faceDO.getId()));
    }
}
