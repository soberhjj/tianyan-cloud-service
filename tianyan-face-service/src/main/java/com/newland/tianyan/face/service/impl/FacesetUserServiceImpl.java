package com.newland.tianyan.face.service.impl;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.newland.tianya.commons.base.exception.BaseException;
import com.newland.tianya.commons.base.model.proto.NLBackend;
import com.newland.tianya.commons.base.support.ExceptionSupport;
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
import com.newland.tianyan.face.event.user.UserCreateEvent;
import com.newland.tianyan.face.event.user.UserDeleteEvent;
import com.newland.tianyan.face.service.FacesetUserService;
import com.newland.tianyan.face.service.GroupInfoService;
import com.newland.tianyan.face.service.IVectorSearchService;
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
import tk.mybatis.mapper.entity.Example;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import static com.newland.tianyan.face.constant.BusinessArgumentConstants.MAX_USER_NUMBER;

/**
 * @Author: huangJunJie  2020-11-07 09:18
 */
@Service
@Slf4j
public class FacesetUserServiceImpl implements FacesetUserService {

    @Autowired
    private UserInfoMapper userInfoMapper;
    @Autowired
    private GroupInfoMapper groupInfoMapper;
    @Autowired
    private GroupInfoService groupInfoService;
    @Autowired
    private FaceMapper faceMapper;
    @Autowired
    private ApplicationEventPublisher publisher;
    @Autowired
    private IVectorSearchService<FaceDO> faceCacheHelper;

    @Override
    public PageInfo<UserInfoDO> getList(NLBackend.BackendAllRequest receive) throws BaseException {
        UserInfoDO query = ProtobufUtils.parseTo(receive, UserInfoDO.class);

        //????????????????????????????????????
        GroupInfoDO groupInfoDO = new GroupInfoDO();
        groupInfoDO.setAppId(query.getAppId());
        groupInfoDO.setGroupId(query.getGroupId());
        groupInfoDO.setIsDelete(EntityStatusConstants.NOT_DELETE);
        groupInfoDO = groupInfoMapper.selectOne(groupInfoDO);
        if (groupInfoDO == null) {
            throw ExceptionSupport.toException(ExceptionEnum.GROUP_NOT_FOUND, query.getGroupId());
        }
        Long appId = groupInfoDO.getAppId();
        Long gid = groupInfoDO.getId();
        return PageHelper.offsetPage(query.getStartIndex(), query.getLength())
                .doSelectPageInfo(
                        () -> {
                            Example example = new Example(UserInfoDO.class);
                            Example.Criteria criteria = example.createCriteria();
                            criteria.andEqualTo("appId", appId);
                            criteria.andEqualTo("gid", gid);
                            // dynamic sql
                            if (query.getUserId() != null) {
                                criteria.andLike("userId", "%" + query.getUserId() + "%");
                            }
                            // execute select
                            userInfoMapper.selectByExample(example);
                        }
                );
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRES_NEW, rollbackFor = Exception.class)
    public void copy(NLBackend.BackendAllRequest receive) throws BaseException {
        UserInfoDO queryUser = ProtobufUtils.parseTo(receive, UserInfoDO.class);
        Long appId = receive.getAppId();
        String userId = receive.getUserId();
        String srcGroupId = receive.getSrcGroupId();
        String dstGroupId = receive.getDstGroupId();

        log.info("????????????-????????????{}??????????????????{}??????????????????", srcGroupId, dstGroupId);
        //???????????????????????????
        GroupInfoDO srcGroupInfo = new GroupInfoDO();
        srcGroupInfo.setAppId(queryUser.getAppId());
        srcGroupInfo.setGroupId(srcGroupId);
        srcGroupInfo.setIsDelete(EntityStatusConstants.NOT_DELETE);
        boolean sourceInvalid = groupInfoMapper.selectCount(srcGroupInfo) > 0;
        if (!sourceInvalid) {
            throw ExceptionSupport.toException(ExceptionEnum.GROUP_NOT_FOUND, srcGroupId);
        }
        //??????????????????????????????
        GroupInfoDO dstGroupInfo = new GroupInfoDO();
        dstGroupInfo.setAppId(queryUser.getAppId());
        dstGroupInfo.setGroupId(dstGroupId);
        dstGroupInfo.setIsDelete(EntityStatusConstants.NOT_DELETE);
        dstGroupInfo = groupInfoMapper.selectOne(dstGroupInfo);
        boolean targetInvalid = dstGroupInfo != null;
        if (!targetInvalid) {
            throw ExceptionSupport.toException(ExceptionEnum.GROUP_NOT_FOUND, dstGroupId);
        }
        queryUser.setGroupId(srcGroupId);
        queryUser.setGid(srcGroupInfo.getId());
        log.info("????????????-?????????????????????{}???????????????", JsonUtils.toJson(queryUser));
        UserInfoDO sourceUserInfoDO = userInfoMapper.selectOne(queryUser);
        if (sourceUserInfoDO == null) {
            throw ExceptionSupport.toException(ExceptionEnum.USER_NOT_FOUND, userId);
        }
        //????????????????????????????????????
        List<FaceDO> srcFace = this.queryFace(appId, srcGroupInfo.getId(), srcGroupId, userId);
        if (CollectionUtils.isEmpty(srcFace)) {
            throw ExceptionSupport.toException(ExceptionEnum.FACE_NOT_FOUND);
        }
        List<FaceDO> insertList = new ArrayList<>(srcFace.size());
        queryUser.setGroupId(dstGroupId);
        queryUser.setGid(dstGroupInfo.getId());
        log.info("????????????-????????????????????????{}???????????????", JsonUtils.toJson(queryUser));
        UserInfoDO targetUserInfoDO = userInfoMapper.selectOne(queryUser);
        log.info("????????????-????????????????????????????????????????????????????????????");
        if (targetUserInfoDO == null) {
            if (dstGroupInfo.getUserNumber() > MAX_USER_NUMBER) {
                throw ExceptionSupport.toException(ExceptionEnum.OVER_USE_MAX_NUMBER);
            }
            UserInfoDO dstUser = new UserInfoDO();
            dstUser.setAppId(sourceUserInfoDO.getAppId());
            dstUser.setGid(dstGroupInfo.getId());
            dstUser.setGroupId(dstGroupInfo.getGroupId());
            dstUser.setUserId(sourceUserInfoDO.getUserId());
            dstUser.setUserName(sourceUserInfoDO.getUserName());
            dstUser.setUserInfo(sourceUserInfoDO.getUserInfo());
            dstUser.setFaceNumber(0);
            userInfoMapper.insertGetId(dstUser);
            publisher.publishEvent(new UserCreateEvent(dstUser.getAppId(), dstUser.getGroupId(), dstUser.getUserId()));
            targetUserInfoDO = dstUser;
        } else {
            log.info("????????????-????????????????????????????????????,????????????????????????");
            //????????????????????????
            List<FaceDO> dstFace = this.queryFace(appId, dstGroupInfo.getId(), dstGroupId, userId);
            //??????????????????
            Set<String> srcImages = srcFace.stream().map(FaceDO::getPhotoSign).collect(Collectors.toSet());
            Set<String> dstImages = dstFace.stream().map(FaceDO::getPhotoSign).collect(Collectors.toSet());
            srcImages.removeAll(dstImages);
            //?????????????????????????????????????????????????????????
            if (CollectionUtils.isEmpty(srcFace)) {
                return;
            }
            //md5??????????????????????????????????????????????????????????????????
            List<FaceDO> srcFaceCopy = new ArrayList<>(srcFace);
            for (FaceDO faceDO : srcFaceCopy) {
                if (StringUtils.isEmpty(faceDO.getPhotoSign()) || srcImages.contains(faceDO.getPhotoSign())) {
                    continue;
                }
                srcFace.remove(faceDO);
            }
        }
        if (!CollectionUtils.isEmpty(srcFace)) {
            //????????????????????????
            long faceCount = 0L;
            FaceIdSlotHelper faceIdSlotHelper = new FaceIdSlotHelper(targetUserInfoDO.getFaceIdSlot());
            for (FaceDO faceDO : srcFace) {
                FaceDO insertFace = this.convertCopyFace(faceDO, faceIdSlotHelper.pollNextValidId(), targetUserInfoDO, dstGroupInfo);
                insertFace.setId(insertFace.getId() + ++faceCount);
                insertList.add(insertFace);
            }
            log.info("????????????-????????????????????????????????????");
            faceMapper.insertBatch(insertList);
            //????????????????????????????????????+0????????????+?????????
            publisher.publishEvent(new FaceCreateEvent(appId, dstGroupId, userId, targetUserInfoDO.getFaceIdSlot(),
                    insertList.stream().map(FaceDO::getId).collect(Collectors.toList())));
            faceCacheHelper.addBatch(insertList);
        }
    }

    private FaceDO convertCopyFace(FaceDO targetFace, Integer nextFaceNo, UserInfoDO userInfoDO, GroupInfoDO targetGroup) {
        FaceDO newFace = new FaceDO();
        Long initId = VectorSearchKeyUtils.generatedKey(targetGroup.getId(), userInfoDO.getId(), nextFaceNo);
        newFace.setId(initId);
        newFace.setAppId(targetGroup.getAppId());
        newFace.setGid(targetGroup.getId());
        newFace.setGroupId(targetGroup.getGroupId());

        newFace.setUid(userInfoDO.getId());
        newFace.setUserId(userInfoDO.getUserId());

        newFace.setFeatures(targetFace.getFeatures());
        newFace.setImagePath(targetFace.getImagePath());
        return newFace;
    }

    /**
     * ?????????????????????????????????
     */
    private List<FaceDO> queryFace(Long appId, Long gid, String groupId, String userId) throws BaseException {
        FaceDO faceQuery = new FaceDO();
        faceQuery.setAppId(appId);
        faceQuery.setGid(gid);
        faceQuery.setGroupId(groupId);
        faceQuery.setUserId(userId);
        return faceMapper.select(faceQuery);
    }

    /**
     * ????????????
     */
    @Override
    @Transactional(propagation = Propagation.REQUIRES_NEW, rollbackFor = Exception.class)
    public void delete(NLBackend.BackendAllRequest receive) throws BaseException {
        if (!StringUtils.isEmpty(receive.getGroupId())) {
            GroupInfoDO dstGroupInfo = new GroupInfoDO();
            dstGroupInfo.setAppId(receive.getAppId());
            dstGroupInfo.setGroupId(receive.getGroupId());
            dstGroupInfo.setIsDelete(EntityStatusConstants.NOT_DELETE);
            dstGroupInfo = groupInfoMapper.selectOne(dstGroupInfo);
            if (dstGroupInfo == null) {
                throw ExceptionSupport.toException(ExceptionEnum.GROUP_NOT_FOUND, receive.getGroupId());
            }
        }

        UserInfoDO query = ProtobufUtils.parseTo(receive, UserInfoDO.class);
        UserInfoDO userInfoDO = userInfoMapper.selectOne(query);
        if (userInfoDO == null) {
            throw ExceptionSupport.toException(ExceptionEnum.USER_NOT_FOUND, receive.getUserId());
        }
        List<Long> faceIdList = faceMapper.selectId(userInfoDO.getGroupId(), userInfoDO.getUserId());

        //???????????????????????????
        userInfoMapper.delete(query);
        publisher.publishEvent(new UserDeleteEvent(userInfoDO.getAppId(), userInfoDO.getGroupId(), faceIdList.size()));
        faceCacheHelper.deleteBatch(userInfoDO.getAppId(), faceIdList);
    }

    /**
     * ?????????user_id????????????????????????????????????????????????????????????app????????????????????????????????????????????????user_id???
     * ????????????user_id???????????????????????????app????????????user???
     */
    @Override
    public List<UserInfoDO> getInfo(NLBackend.BackendAllRequest receive) throws BaseException {
        Set<String> groupIdSet = new HashSet<>();
        Long appId = receive.getAppId();
        String groupId = receive.getGroupId();
        String userId = receive.getUserId();

        List<UserInfoDO> userInfoDOList = new ArrayList<>();
        //????????????????????????????????????
        if (StringUtils.isEmpty(groupId)) {
            UserInfoDO userInfoDO = UserInfoDO.builder()
                    .appId(appId)
                    .userId(userId)
                    .build();
            userInfoDOList.addAll(userInfoMapper.select(userInfoDO));
            if (CollectionUtils.isEmpty(userInfoDOList)) {
                throw ExceptionSupport.toException(ExceptionEnum.USER_NOT_FOUND, userId);
            }
            Set<String> groupIds = userInfoDOList.stream().map(UserInfoDO::getGroupId).collect(Collectors.toSet());
            groupIdSet.addAll(groupIds);
            this.getValidGroupList(appId, groupIdSet);
        } else {
            //?????????????????????????????????
            groupIdSet.add(groupId);
            Set<Long> gidIdSet = this.getValidGroupList(appId, groupIdSet);
            userInfoDOList.addAll(userInfoMapper.queryBatch(receive.getAppId(), gidIdSet, null, receive.getUserId()));
            if (CollectionUtils.isEmpty(userInfoDOList)) {
                throw ExceptionSupport.toException(ExceptionEnum.USER_NOT_FOUND, userId);
            }
        }

        userInfoDOList.forEach(item -> {
            item.setModifyTime(null);
            item.setCreateTime(null);
        });
        return userInfoDOList;
    }

    /**
     * ??????appId??????group???databaseId??????
     */
    private Set<Long> getValidGroupList(Long appId, Set<String> groupIdSet) throws BaseException {
        Set<Long> result = new HashSet<>();
        if (!CollectionUtils.isEmpty(groupIdSet)) {
            List<GroupInfoDO> groupInfoDOList = groupInfoService.queryBatch(appId, groupIdSet);
            result = groupInfoDOList.stream().map(GroupInfoDO::getId).collect(Collectors.toSet());
        }
        //???????????????????????????id??????
        if (CollectionUtils.isEmpty(result)) {
            throw ExceptionSupport.toException(ExceptionEnum.GROUP_NOT_FOUND, CollectionUtils.isEmpty(groupIdSet) ? null : groupIdSet.toString());
        }
        return result;
    }

}
