package com.newland.tianyan.face.service.impl;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.newland.tianyan.common.exception.BaseException;
import com.newland.tianyan.common.utils.JsonUtils;
import com.newland.tianyan.common.utils.ProtobufUtils;
import com.newland.tianyan.common.utils.message.NLBackend;
import com.newland.tianyan.face.constant.BusinessErrorEnums;
import com.newland.tianyan.face.constant.EntityStatusConstants;
import com.newland.tianyan.face.constant.SystemErrorEnums;
import com.newland.tianyan.face.dao.FaceMapper;
import com.newland.tianyan.face.dao.GroupInfoMapper;
import com.newland.tianyan.face.dao.UserInfoMapper;
import com.newland.tianyan.face.domain.entity.FaceDO;
import com.newland.tianyan.face.domain.entity.GroupInfoDO;
import com.newland.tianyan.face.domain.entity.UserInfoDO;
import com.newland.tianyan.face.event.user.UserCopyEvent;
import com.newland.tianyan.face.event.user.UserDeleteEvent;
import com.newland.tianyan.face.service.FacesetUserService;
import com.newland.tianyan.face.service.IVectorSearchService;
import com.newland.tianyan.face.utils.VectorSearchKeyUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import tk.mybatis.mapper.entity.Example;

import java.util.ArrayList;
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

        return PageHelper.offsetPage(query.getStartIndex(), query.getLength())
                .doSelectPageInfo(
                        () -> {
                            Example example = new Example(UserInfoDO.class);
                            Example.Criteria criteria = example.createCriteria();

                            criteria.andEqualTo("appId", query.getAppId());
                            criteria.andEqualTo("groupId", query.getGroupId());

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
    public void copy(NLBackend.BackendAllRequest receive) throws BaseException {
        UserInfoDO queryUser = ProtobufUtils.parseTo(receive, UserInfoDO.class);
        Long appId = receive.getAppId();
        String userId = receive.getUserId();
        String srcGroupId = receive.getSrcGroupId();
        String dstGroupId = receive.getDstGroupId();

        log.info("人脸复制-源用户组{}和目标用户组{}的有效性检查", srcGroupId, dstGroupId);
        GroupInfoDO srcGroupInfo = new GroupInfoDO();
        srcGroupInfo.setAppId(queryUser.getAppId());
        srcGroupInfo.setGroupId(srcGroupId);
        srcGroupInfo.setIsDelete(EntityStatusConstants.NOT_DELETE);
        boolean sourceInvalid = groupInfoMapper.selectCount(srcGroupInfo) > 0;
        if (!sourceInvalid) {
            throw BusinessErrorEnums.GROUP_NOT_FOUND.toException(srcGroupId);
        }
        GroupInfoDO dstGroupInfo = new GroupInfoDO();
        dstGroupInfo.setAppId(queryUser.getAppId());
        dstGroupInfo.setGroupId(dstGroupId);
        dstGroupInfo.setIsDelete(EntityStatusConstants.NOT_DELETE);
        dstGroupInfo = groupInfoMapper.selectOne(dstGroupInfo);
        boolean targetInvalid = dstGroupInfo != null;
        if (!targetInvalid) {
            throw BusinessErrorEnums.GROUP_NOT_FOUND.toException(dstGroupId);
        }

        queryUser.setGroupId(srcGroupId);
        log.info("人脸复制-源用户组的用户{}有效性检查", JsonUtils.toJson(queryUser));
        UserInfoDO sourceUserInfoDO = userInfoMapper.selectOne(queryUser);
        if (sourceUserInfoDO == null) {
            throw BusinessErrorEnums.USER_NOT_FOUND.toException(userId);
        }
        //待复制的源用户的人脸资料
        List<FaceDO> srcFace = this.queryFace(appId, srcGroupId, userId);
        if (CollectionUtils.isEmpty(srcFace)) {
            throw BusinessErrorEnums.FACE_NOT_FOUND.toException();
        }
        List<FaceDO> insertList = new ArrayList<>(srcFace.size());
        queryUser.setGroupId(dstGroupId);
        log.info("人脸复制-目标用户组的用户{}有效性检查", JsonUtils.toJson(queryUser));
        UserInfoDO targetUserInfoDO = userInfoMapper.selectOne(queryUser);
        int faceNumber, userNumber;
        log.info("人脸复制-目标用户组的不存在同名用户，开始新建用户");
        if (targetUserInfoDO == null) {
            if (dstGroupInfo.getUserNumber() > MAX_USER_NUMBER) {
                throw BusinessErrorEnums.OVER_USE_MAX_NUMBER.toException();
            }
            userNumber = 1;
            faceNumber = srcFace.size();
            UserInfoDO dstUser = new UserInfoDO();
            dstUser.setAppId(sourceUserInfoDO.getAppId());
            dstUser.setGid(dstGroupInfo.getId());
            dstUser.setGroupId(dstGroupInfo.getGroupId());
            dstUser.setUserId(sourceUserInfoDO.getUserId());
            dstUser.setUserName(sourceUserInfoDO.getUserName());
            dstUser.setUserInfo(sourceUserInfoDO.getUserInfo());
            dstUser.setFaceNumber(sourceUserInfoDO.getFaceNumber());
            userInfoMapper.insertGetId(dstUser);
            targetUserInfoDO = dstUser;
        } else {
            userNumber = 0;
            log.info("人脸复制-目标用户组的存在同名用户,开始过滤相同人脸");
            //封装复制人脸信息
            List<FaceDO> dstFace = this.queryFace(appId, dstGroupId, userId);
            //过滤相同照片
            Set<String> srcImages = srcFace.stream().map(FaceDO::getImagePath).collect(Collectors.toSet());
            Set<String> dstImages = dstFace.stream().map(FaceDO::getImagePath).collect(Collectors.toSet());
            srcImages.removeAll(dstImages);
            //目标用户组的资料已和源用户组的资料一致
            if (CollectionUtils.isEmpty(srcFace)) {
                return;
            }
            faceNumber = srcFace.size();
            List<FaceDO> srcFaceCopy = new ArrayList<>(srcFace);
            for (FaceDO faceDO : srcFaceCopy) {
                if (!srcImages.contains(faceDO.getImagePath())) {
                    srcFace.remove(faceDO);
                }
            }
        }
        //封装复制人脸信息
        for (FaceDO faceDO : srcFace) {
            FaceDO insertFace = this.convertCopyFace(faceDO, targetUserInfoDO, dstGroupInfo);
            insertList.add(insertFace);
        }
        log.info("人脸复制-请求向量搜索服务添加人脸");
        faceCacheHelper.addBatch(insertList);
        try {
            faceMapper.insertBatch(insertList);
        } catch (Exception e) {
            throw SystemErrorEnums.DB_INSERT_ERROR.toException(JsonUtils.toJson(insertList));
        }
        //存在同名用户：组下用户数+0，人脸数+新增数
        publisher.publishEvent(new UserCopyEvent(appId, dstGroupId, userId, faceNumber, userNumber));
    }

    private FaceDO convertCopyFace(FaceDO targetFace, UserInfoDO userInfoDO, GroupInfoDO targetGroup) {
        FaceDO newFace = new FaceDO();
        newFace.setId(VectorSearchKeyUtils.generatedKey(targetGroup.getId(), userInfoDO.getId(), userInfoDO.getFaceNumber() + 1));
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
    private List<FaceDO> queryFace(Long appId, String groupId, String userId) throws BaseException {
        FaceDO faceQuery = new FaceDO();
        faceQuery.setAppId(appId);
        faceQuery.setGroupId(groupId);
        faceQuery.setUserId(userId);
        PageInfo<FaceDO> allFace = PageHelper.offsetPage(faceQuery.getStartIndex(), faceQuery.getLength())
                .setOrderBy("create_time desc")
                .doSelectPageInfo(
                        () -> faceMapper.select(faceQuery));
        return allFace.getList();
    }

    /**
     * 删除用户
     */
    @Override
    public void delete(NLBackend.BackendAllRequest receive) throws BaseException {
        UserInfoDO query = ProtobufUtils.parseTo(receive, UserInfoDO.class);

        String[] groups = query.getGroupId().split(",");
        for (String group : groups) {
            query.setGroupId(group);

            UserInfoDO userInfoDO = new UserInfoDO();
            userInfoDO.setUserId(query.getUserId());
            userInfoDO.setAppId(query.getAppId());
            userInfoDO.setGroupId(query.getGroupId());
            userInfoDO = userInfoMapper.selectOne(userInfoDO);
            if (userInfoDO == null) {
                throw BusinessErrorEnums.USER_NOT_FOUND.toException(receive.getUserId());
            }

            //缓存中删除用户的所有人脸
            List<Long> faceIdList = faceMapper.selectIdByUserId(userInfoDO.getUserId());
            faceCacheHelper.deleteBatch(query.getAppId(), faceIdList);
            //物理删除用户及人脸
            int userCount;
            userCount = userInfoMapper.delete(query);
            if (userCount < 0) {
                throw SystemErrorEnums.DB_DELETE_ERROR.toException(JsonUtils.toJson(query));
            }
            FaceDO faceDO = new FaceDO();
            faceDO.setGroupId(group);
            faceDO.setUserId(userInfoDO.getUserId());
            faceDO.setAppId(receive.getAppId());
            if (faceMapper.delete(faceDO) < 0) {
                throw SystemErrorEnums.DB_DELETE_ERROR.toException(JsonUtils.toJson(faceDO));
            }

            publisher.publishEvent(new UserDeleteEvent(query.getAppId(), query.getGroupId(), query.getUserId(), userInfoDO.getFaceNumber(), userCount));

        }
    }

    /**
     * 注意：user_id只是在一个用户组里是唯一的，也就是说一个app下的不用的用户组中可以存在相同的user_id。
     * 换句话说user_id并不能唯一表示某个app下的某个user。
     */
    @Override
    public List<UserInfoDO> getInfo(NLBackend.BackendAllRequest receive) throws BaseException {
        UserInfoDO userQuery = ProtobufUtils.parseTo(receive, UserInfoDO.class);
        //如果传入的参数中没有传入用户组的话，那么就根据传入的app_id去获取该app的所有用户组。
        List<String> groupIds = this.getGroupList(userQuery.getUserId(), userQuery.getAppId());
        if (CollectionUtils.isEmpty(groupIds)) {
            return new ArrayList<>();
        }
        List<UserInfoDO> userList = new ArrayList<>();
        for (String groupId : groupIds) {
            //user表索引userId、groupId
            userQuery.setUserId(userQuery.getUserId());
            userQuery.setGroupId(groupId);

            //接下来就是去user_info表中根据app_id和group_id和user_id查询用户信息
            UserInfoDO userInfoDO = userInfoMapper.selectOne(userQuery);
            if (userInfoDO != null) {
                userList.add(userInfoDO);
            }
        }
        return userList;
    }

    /**
     * 根据appId获取group的databaseId列表
     */
    private List<String> getGroupList(String userId, Long appId) throws BaseException {
        //获取用户所属的组列表
        List<String> groupIdList = userInfoMapper.getGroupIdByUserId(userId, appId);
        //仅返回有效状态的组id信息
        List<String> result = new ArrayList<>();
        if (!CollectionUtils.isEmpty(groupIdList)) {
            GroupInfoDO groupInfoDO = new GroupInfoDO();
            for (String groupId : groupIdList) {
                groupInfoDO.setAppId(appId);
                groupInfoDO.setGroupId(groupId);
                groupInfoDO.setIsDelete(EntityStatusConstants.NOT_DELETE);
                if (groupInfoMapper.selectCount(groupInfoDO) > 0) {
                    result.add(groupId);
                }
            }
        }
        return result;
    }
}
