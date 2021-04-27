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

        //过滤掉用户组不合法的请求
        GroupInfoDO groupInfoDO = new GroupInfoDO();
        groupInfoDO.setAppId(query.getAppId());
        groupInfoDO.setGroupId(query.getGroupId());
        groupInfoDO.setIsDelete(EntityStatusConstants.NOT_DELETE);
        if (groupInfoMapper.selectCount(groupInfoDO) <= 0) {
            return new PageInfo<>(new ArrayList<>());
        }

        return PageHelper.startPage(query.getStartIndex() + 1, query.getLength())
                .doSelectPageInfo(
                        () -> {
                            Example example = new Example(UserInfoDO.class);
                            Example.Criteria criteria = example.createCriteria();
                            criteria.andEqualTo("appId", query.getAppId());
                            criteria.andEqualTo("groupId", query.getGroupId());
                            criteria.andEqualTo("gid", query.getGid());
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

        log.info("人脸复制-源用户组{}和目标用户组{}的有效性检查", srcGroupId, dstGroupId);
        //源用户组有效性检查
        GroupInfoDO srcGroupInfo = new GroupInfoDO();
        srcGroupInfo.setAppId(queryUser.getAppId());
        srcGroupInfo.setGroupId(srcGroupId);
        srcGroupInfo.setIsDelete(EntityStatusConstants.NOT_DELETE);
        boolean sourceInvalid = groupInfoMapper.selectCount(srcGroupInfo) > 0;
        if (!sourceInvalid) {
            throw ExceptionSupport.toException(ExceptionEnum.GROUP_NOT_FOUND, srcGroupId);
        }
        //目标用户组有效性检查
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
        log.info("人脸复制-源用户组的用户{}有效性检查", JsonUtils.toJson(queryUser));
        UserInfoDO sourceUserInfoDO = userInfoMapper.selectOne(queryUser);
        if (sourceUserInfoDO == null) {
            throw ExceptionSupport.toException(ExceptionEnum.USER_NOT_FOUND, userId);
        }
        //待复制的源用户的人脸资料
        List<FaceDO> srcFace = this.queryFace(appId, srcGroupInfo.getId(), srcGroupId, userId);
        if (CollectionUtils.isEmpty(srcFace)) {
            throw ExceptionSupport.toException(ExceptionEnum.FACE_NOT_FOUND);
        }
        List<FaceDO> insertList = new ArrayList<>(srcFace.size());
        queryUser.setGroupId(dstGroupId);
        queryUser.setGid(dstGroupInfo.getId());
        log.info("人脸复制-目标用户组的用户{}有效性检查", JsonUtils.toJson(queryUser));
        UserInfoDO targetUserInfoDO = userInfoMapper.selectOne(queryUser);
        log.info("人脸复制-目标用户组的不存在同名用户，开始新建用户");
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
            log.info("人脸复制-目标用户组的存在同名用户,开始过滤相同人脸");
            //封装复制人脸信息
            List<FaceDO> dstFace = this.queryFace(appId, dstGroupInfo.getId(), dstGroupId, userId);
            //过滤相同照片
            Set<String> srcImages = srcFace.stream().map(FaceDO::getImagePath).collect(Collectors.toSet());
            Set<String> dstImages = dstFace.stream().map(FaceDO::getImagePath).collect(Collectors.toSet());
            srcImages.removeAll(dstImages);
            //目标用户组的资料已和源用户组的资料一致
            if (CollectionUtils.isEmpty(srcFace)) {
                return;
            }
            List<FaceDO> srcFaceCopy = new ArrayList<>(srcFace);
            for (FaceDO faceDO : srcFaceCopy) {
                if (!srcImages.contains(faceDO.getImagePath())) {
                    srcFace.remove(faceDO);
                }
            }
        }
        if(!CollectionUtils.isEmpty(srcFace)){
            //封装复制人脸信息
            long faceCount = 0L;
            FaceIdSlotHelper faceIdSlotHelper = new FaceIdSlotHelper(targetUserInfoDO.getFaceIdSlot());
            for (FaceDO faceDO : srcFace) {
                FaceDO insertFace = this.convertCopyFace(faceDO, faceIdSlotHelper.pollNextValidId(), targetUserInfoDO, dstGroupInfo);
                insertFace.setId(insertFace.getId() + ++faceCount);
                insertList.add(insertFace);
            }
            log.info("人脸复制-请求向量搜索服务添加人脸");
            faceMapper.insertBatch(insertList);
            //存在同名用户：组下用户数+0，人脸数+新增数
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
     * 获取人脸库中的人脸信息
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
     * 删除用户
     */
    @Override
    @Transactional(propagation = Propagation.REQUIRES_NEW, rollbackFor = Exception.class)
    public void delete(NLBackend.BackendAllRequest receive) throws BaseException {
        GroupInfoDO dstGroupInfo = new GroupInfoDO();
        dstGroupInfo.setAppId(receive.getAppId());
        dstGroupInfo.setGroupId(receive.getGroupId());
        dstGroupInfo.setIsDelete(EntityStatusConstants.NOT_DELETE);
        dstGroupInfo = groupInfoMapper.selectOne(dstGroupInfo);
        boolean targetInvalid = dstGroupInfo != null;
        if (!targetInvalid) {
            throw ExceptionSupport.toException(ExceptionEnum.GROUP_NOT_FOUND, receive.getGroupId());
        }

        UserInfoDO query = ProtobufUtils.parseTo(receive, UserInfoDO.class);
        UserInfoDO userInfoDO = userInfoMapper.selectOne(query);
        if (userInfoDO == null) {
            throw ExceptionSupport.toException(ExceptionEnum.USER_NOT_FOUND, receive.getUserId());
        }
        List<Long> faceIdList = faceMapper.selectId(userInfoDO.getGroupId(), userInfoDO.getUserId());

        //物理删除用户及人脸
        userInfoMapper.delete(query);
        publisher.publishEvent(new UserDeleteEvent(userInfoDO.getAppId(), userInfoDO.getGroupId(), faceIdList.size()));
        faceCacheHelper.deleteBatch(userInfoDO.getAppId(), faceIdList);
    }

    /**
     * 注意：user_id只是在一个用户组里是唯一的，也就是说一个app下的不用的用户组中可以存在相同的user_id。
     * 换句话说user_id并不能唯一表示某个app下的某个user。
     */
    @Override
    public List<UserInfoDO> getInfo(NLBackend.BackendAllRequest receive) throws BaseException {
        UserInfoDO userQuery = ProtobufUtils.parseTo(receive, UserInfoDO.class);
        //如果传入的参数中没有传入用户组的话，那么就根据传入的app_id去获取该app的所有用户组。
        Set<Long> gidIdSet = this.getValidGroupList(receive.getAppId(), receive.getGroupId(), userQuery.getUserId());
        List<UserInfoDO> userInfoList = userInfoMapper.queryBatch(receive.getAppId(), gidIdSet, null, receive.getUserId());
        if (CollectionUtils.isEmpty(userInfoList)) {
            throw ExceptionSupport.toException(ExceptionEnum.USER_NOT_FOUND, receive.getUserId());
        }
        userInfoList.forEach(item -> {
            item.setModifyTime(null);
            item.setCreateTime(null);
        });
        return userInfoList;
    }

    /**
     * 根据appId获取group的databaseId列表
     */
    private Set<Long> getValidGroupList(Long appId, String groupId, String userId) throws BaseException {
        //获取用户所属的组列表
        Set<String> groupIdSet = userInfoMapper.getGroupId(appId, groupId, userId);
        Set<Long> result = new HashSet<>();
        if (!CollectionUtils.isEmpty(groupIdSet)) {
            List<GroupInfoDO> groupInfoDOList = groupInfoService.queryBatch(appId, groupIdSet);
            result = groupInfoDOList.stream().map(GroupInfoDO::getId).collect(Collectors.toSet());
        }
        //仅返回有效状态的组id信息
        if (CollectionUtils.isEmpty(result)) {
            throw ExceptionSupport.toException(ExceptionEnum.GROUP_NOT_FOUND, !StringUtils.isEmpty(groupId) ? groupId : !CollectionUtils.isEmpty(groupIdSet) ? groupIdSet.toString() : null);
        }
        return result;
    }
}
