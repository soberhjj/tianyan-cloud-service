package com.newland.tianyan.face.service.impl;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.newland.tianya.commons.base.model.imagestrore.DownloadResDTO;
import com.newland.tianya.commons.base.model.imagestrore.UploadResDTO;
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
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

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
        //????????????
        String image = ImageCheckUtils.imageCheckAndFormatting(receive.getImage());
        String actionType = receive.getActionType();
        qualityCheckService.checkQuality(receive.getQualityControl(), image);

        FaceDO insertFaceDO = new FaceDO();
        log.info("????????????-???????????????????????????");
        UploadResDTO uploadResDTO = imageStorageService.upload(image);
        insertFaceDO.setImagePath(uploadResDTO.getImagePath());
        insertFaceDO.setPhotoSign(uploadResDTO.getImagePath());
        log.info("????????????-?????????????????????");
        this.handleFeatures(insertFaceDO, image);
        insertFaceDO.setAppId(receive.getAppId());

        Long appId = receive.getAppId();
        String groupId = receive.getGroupId();
        String userId = receive.getUserId();
        //???????????????????????????????????????
        GroupInfoDO groupInfoDO = this.getExistedGroup(appId, groupId);
        insertFaceDO.setGid(groupInfoDO.getId());
        insertFaceDO.setGroupId(groupInfoDO.getGroupId());
        //????????????????????????????????????
        UserInfoDO queryUser = ProtobufUtils.parseTo(receive, UserInfoDO.class);
        queryUser.setGid(groupInfoDO.getId());
        UserInfoDO userInfoDO = this.getExistedUser(queryUser);
        if (userInfoDO.getFaceNumber() >= MAX_FACE_NUMBER) {
            throw ExceptionSupport.toException(ExceptionEnum.OVER_FACE_MAX_NUMBER);
        }
        insertFaceDO.setUid(userInfoDO.getId());
        insertFaceDO.setUserId(userInfoDO.getUserId());
        FaceIdSlotHelper faceIdSlotHelper = new FaceIdSlotHelper(userInfoDO.getFaceIdSlot());
        insertFaceDO.setId(VectorSearchKeyUtils.generatedKey(insertFaceDO.getGid(), insertFaceDO.getUid(), faceIdSlotHelper.pollNextValidId()));

        //????????????????????????????????????
        if (userInfoDO.getFaceNumber() == 0) {
            log.info("????????????-????????????append");
            faceMapper.insertSelective(insertFaceDO);
            publisher.publishEvent(new UserCreateEvent(appId, groupId, userId));
            publisher.publishEvent(new FaceCreateEvent(appId, groupId, userId,
                    userInfoDO.getFaceIdSlot(), insertFaceDO.getId()));
            faceCacheHelper.add(insertFaceDO);
        } else {

            if (ACTION_TYPE_APPEND.equals(actionType) || StringUtils.isEmpty(actionType)) {
                log.info("????????????-????????????append");
                faceMapper.insertSelective(insertFaceDO);
                publisher.publishEvent(new FaceCreateEvent(appId, groupId, userId,
                        userInfoDO.getFaceIdSlot(), insertFaceDO.getId()));
                faceCacheHelper.add(insertFaceDO);
            } else if (ACTION_TYPE_REPLACE.equals(actionType)) {
                log.info("????????????-???????????????????????????replace");
                //????????????
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

    private GroupInfoDO getExistedGroup(Long appId, String groupId) {
        GroupInfoDO groupInfoDO = new GroupInfoDO();
        groupInfoDO.setAppId(appId);
        groupInfoDO.setGroupId(groupId);
        groupInfoDO.setIsDelete(EntityStatusConstants.NOT_DELETE);
        groupInfoDO = groupInfoMapper.selectOne(groupInfoDO);
        if (groupInfoDO == null) {
            log.info("????????????-????????????????????????{}", appId + "/" + groupId);
            GroupInfoDO insertGroup = new GroupInfoDO();
            insertGroup.setAppId(appId);
            insertGroup.setGroupId(groupId);
            insertGroup.setIsDelete(EntityStatusConstants.NOT_DELETE);
            insertGroup.setUserNumber(0);
            insertGroup.setFaceNumber(0);
            groupInfoMapper.insertGetId(insertGroup);
            publisher.publishEvent(new GroupCreateEvent(appId, groupId));
            log.info("????????????-?????????????????????{}", JsonUtils.toJson(insertGroup));
            groupInfoDO = insertGroup;
        }
        if (groupInfoDO.getUserNumber() > MAX_USER_NUMBER) {
            throw ExceptionSupport.toException(ExceptionEnum.OVER_USE_MAX_NUMBER);
        }
        return groupInfoDO;
    }

    private UserInfoDO getExistedUser(UserInfoDO sourceUser) {
        UserInfoDO queryResult = new UserInfoDO();
        queryResult.setAppId(sourceUser.getAppId());
        queryResult.setGid(sourceUser.getGid());
        queryResult.setUserId(sourceUser.getUserId());
        queryResult = userInfoMapper.selectOne(queryResult);
        if (queryResult == null) {
            log.info("????????????-?????????????????????{}", JsonUtils.toJson(queryResult));
            UserInfoDO userInfoDO = new UserInfoDO();
            userInfoDO.setAppId(sourceUser.getAppId());
            userInfoDO.setGid(sourceUser.getGid());
            userInfoDO.setGroupId(sourceUser.getGroupId());
            userInfoDO.setUserId(sourceUser.getUserId());
            String userInfo = sourceUser.getUserInfo();
            userInfoDO.setUserInfo(StringUtils.isEmpty(userInfo) ? " " : userInfo);
            userInfoDO.setFaceNumber(0);
            if (!StringUtils.isEmpty(sourceUser.getUserName())) {
                userInfoDO.setUserName(sourceUser.getUserName());
            } else {
                userInfoDO.setUserName(sourceUser.getUserId());
            }
            userInfoMapper.insertGetId(userInfoDO);
            log.info("????????????-??????????????????{}", JsonUtils.toJson(userInfoDO));
            queryResult = userInfoDO;
        }
        return queryResult;
    }

    private void handleFeatures(FaceDO faceDO, String image) {
        NLFace.CloudFaceSendMessage feature =
                iMqMessageService.amqpHelper(image, 1, 2);
        NLFace.CloudFaceSendMessage.Builder builder = feature.toBuilder();
        List<Float> featureList = builder.getFeatureResult(0).getFeaturesList();
        faceDO.setFeatures(FeaturesTool.normalizeConvertToByte(featureList));
    }

    @Override
    public List<FaceDO> getList(NLBackend.BackendAllRequest receive) throws IOException {
        Long appId = receive.getAppId();
        String groupId = receive.getGroupId();
        String userId = receive.getUserId();
        GroupInfoDO groupInfoDO = new GroupInfoDO();
        groupInfoDO.setAppId(appId);
        groupInfoDO.setGroupId(groupId);
        groupInfoDO.setIsDelete(EntityStatusConstants.NOT_DELETE);
        groupInfoDO = groupInfoMapper.selectOne(groupInfoDO);
        if (groupInfoDO == null) {
            throw ExceptionSupport.toException(ExceptionEnum.GROUP_NOT_FOUND, receive.getGroupId());
        }

        UserInfoDO userInfoDO = UserInfoDO.builder()
                .appId(appId)
                .gid(groupInfoDO.getId())
                .userId(userId)
                .build();
        if (userInfoMapper.selectCount(userInfoDO) == 0) {
            throw ExceptionSupport.toException(ExceptionEnum.USER_NOT_FOUND, receive.getUserId());
        }

        PageInfo<FaceDO> facePageInfo = PageHelper.offsetPage(receive.getStartIndex() , receive.getLength())
                .setOrderBy("create_time desc")
                .doSelectPageInfo(
                        () -> {
                            FaceDO query = FaceDO.builder()
                                    .userId(userId)
                                    .appId(appId)
                                    .groupId(groupId)
                                    .build();
                            faceMapper.select(query);
                        });

        List<FaceDO> faceList = facePageInfo.getList();
        if (CollectionUtils.isEmpty(faceList)) {
            return faceList;
        }

        List<String> imagePath = faceList.stream()
                .map(FaceDO::getImagePath)
                .filter(item -> !StringUtils.isEmpty(item))
                .collect(Collectors.toList());
        List<DownloadResDTO> downloadResDTOList = imageStorageService.batchDownload(imagePath);
        if (!CollectionUtils.isEmpty(downloadResDTOList)) {
            List<String> images = downloadResDTOList.stream().map(DownloadResDTO::getImage).collect(Collectors.toList());
            Map<String, String> pathKeyImageValues = new HashMap<>(images.size());
            for (int i = 0; i < images.size(); i++) {
                pathKeyImageValues.put(imagePath.get(i), images.get(i));
            }

            faceList.forEach(faceItem -> {
                String key = faceItem.getImagePath();
                if (pathKeyImageValues.containsKey(key)) {
                    faceItem.setImage(pathKeyImageValues.get(key));
                }
            });
        }
        return faceList;
    }

    /**
     * ????????????
     */
    @Override
    @Transactional(propagation = Propagation.REQUIRES_NEW, rollbackFor = Exception.class)
    public void delete(NLBackend.BackendAllRequest receive) {
        FaceDO query = ProtobufUtils.parseTo(receive, FaceDO.class);
        query.setId(Long.parseLong(query.getFaceId()));
        Long appId = receive.getAppId();
        String groupId = receive.getGroupId();
        String userId = receive.getUserId();
        Long gid = null;
        //??????group_info?????????????????????group_id
        if (!StringUtils.isEmpty(groupId)) {
            GroupInfoDO groupInfoDO = new GroupInfoDO();
            groupInfoDO.setAppId(appId);
            groupInfoDO.setGroupId(groupId);
            groupInfoDO.setIsDelete(EntityStatusConstants.NOT_DELETE);
            groupInfoDO = groupInfoMapper.selectOne(groupInfoDO);
            if (groupInfoDO == null) {
                throw ExceptionSupport.toException(ExceptionEnum.GROUP_NOT_FOUND, query.getGroupId());
            }
            gid = groupInfoDO.getId();
        }

        //??????user_info?????????????????????user_id
        UserInfoDO userInfoDO = new UserInfoDO();
        userInfoDO.setAppId(appId);
        if (gid != null) {
            userInfoDO.setGid(gid);
        }
        userInfoDO.setUserId(userId);
        userInfoDO = userInfoMapper.selectOne(userInfoDO);
        if (userInfoDO == null) {
            throw ExceptionSupport.toException(ExceptionEnum.USER_NOT_FOUND, userId);
        }

        //???????????????face????????????????????????????????????????????????????????????????????????????????????????????????????????????
        FaceDO queryFace = new FaceDO();
        BeanUtils.copyProperties(userInfoDO, queryFace);
        queryFace.setId(null);
        FaceDO faceDO = faceMapper.selectOne(queryFace);
        if (faceDO == null) {
            throw ExceptionSupport.toException(ExceptionEnum.FACE_NOT_FOUND);
        }

        //????????????????????????????????????
        faceCacheHelper.delete(faceDO.getAppId(), faceDO.getId());
        //??????????????????
        faceMapper.delete(query);

        publisher.publishEvent(new FaceDeleteEvent(userInfoDO.getAppId(), userInfoDO.getGroupId(), userInfoDO.getUserId(),
                1, userInfoDO.getFaceIdSlot(), faceDO.getId()));
    }
}
