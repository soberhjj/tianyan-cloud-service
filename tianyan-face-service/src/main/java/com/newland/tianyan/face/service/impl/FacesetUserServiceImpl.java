package com.newland.tianyan.face.service.impl;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.newland.tianyan.common.generator.IDUtil;
import com.newland.tianyan.common.utils.message.NLBackend;
import com.newland.tianyan.common.utils.ProtobufUtils;
import com.newland.tianyan.face.domain.entity.FaceDO;
import com.newland.tianyan.face.domain.entity.UserInfoDO;
import com.newland.tianyan.face.service.cache.ICacheHelper;
import com.newland.tianyan.face.constant.StatusConstants;
import com.newland.tianyan.face.dao.FaceMapper;
import com.newland.tianyan.face.dao.GroupInfoMapper;
import com.newland.tianyan.face.dao.UserInfoMapper;
import com.newland.tianyan.face.domain.entity.GroupInfoDO;
import com.newland.tianyan.face.event.user.UserCopyEvent;
import com.newland.tianyan.face.event.user.UserDeleteEvent;
import com.newland.tianyan.face.exception.ApiReturnErrorCode;
import com.newland.tianyan.face.service.FacesetUserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import tk.mybatis.mapper.entity.Example;

import javax.persistence.EntityExistsException;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

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
    private ICacheHelper<FaceDO> faceCacheHelper;

    @Override
    public PageInfo<UserInfoDO> getList(NLBackend.BackendAllRequest receive) {
        UserInfoDO query = ProtobufUtils.parseTo(receive, UserInfoDO.class);

        //过滤掉用户组不合法的请求
        GroupInfoDO groupInfoDO = new GroupInfoDO();
        groupInfoDO.setAppId(query.getAppId());
        groupInfoDO.setGroupId(query.getGroupId());
        groupInfoDO.setIsDelete(StatusConstants.NOT_DELETE);
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
    public void copy(NLBackend.BackendAllRequest receive) {
        UserInfoDO queryUser = ProtobufUtils.parseTo(receive, UserInfoDO.class);
        Long appId = receive.getAppId();
        String userId = receive.getUserId();
        String srcGroupId = receive.getSrcGroupId();
        String dstGroupId = receive.getDstGroupId();

        // 源用户组与目标用户组的状态有效性检查
        GroupInfoDO srcGroupInfoDODO = new GroupInfoDO();
        srcGroupInfoDODO.setAppId(queryUser.getAppId());
        srcGroupInfoDODO.setGroupId(srcGroupId);
        srcGroupInfoDODO.setIsDelete(StatusConstants.NOT_DELETE);
        boolean sourceInvalid = groupInfoMapper.selectCount(srcGroupInfoDODO) > 0;
        if (!sourceInvalid) {
            throw new EntityExistsException("group_id " + srcGroupId + " doesn't exist!");
        }
        GroupInfoDO dstGroupInfoDODO = new GroupInfoDO();
        dstGroupInfoDODO.setAppId(queryUser.getAppId());
        dstGroupInfoDODO.setGroupId(dstGroupId);
        dstGroupInfoDODO.setIsDelete(StatusConstants.NOT_DELETE);
        dstGroupInfoDODO = groupInfoMapper.selectOne(dstGroupInfoDODO);
        boolean targetInvalid = dstGroupInfoDODO != null;
        if (!targetInvalid) {
            throw new EntityExistsException("group_id " + dstGroupId + " doesn't exist!");
        }

        //查询源用户组中是否存在当前用户
        queryUser.setGroupId(srcGroupId);
        UserInfoDO sourceUserInfoDO = userInfoMapper.selectOne(queryUser);
        if (sourceUserInfoDO == null) {
            throw new EntityExistsException(userId + " doesn't exist in " + srcGroupId + "!");
        }
        //待复制的源用户的人脸资料
        List<FaceDO> srcFaceDODOS = this.queryFace(appId, srcGroupId, userId);
        if (CollectionUtils.isEmpty(srcFaceDODOS)) {
            throw new EntityExistsException(userId + " doesn't has faces in " + srcGroupId + "!");
        }
        List<FaceDO> insertList = new ArrayList<>(srcFaceDODOS.size());
        //查询目标用户组中是否存在当前用户
        queryUser.setGroupId(dstGroupId);
        UserInfoDO targetUserInfoDO = userInfoMapper.selectOne(queryUser);
        int faceNumber, userNumber;
        //不存在同名用户,直接新建
        if (targetUserInfoDO == null) {
            userNumber = 1;
            faceNumber = srcFaceDODOS.size();
            //新建目标用户组中的用户
            UserInfoDO dstUser = new UserInfoDO();
            dstUser.setAppId(sourceUserInfoDO.getAppId());
            dstUser.setGid(dstGroupInfoDODO.getId());
            dstUser.setGroupId(dstGroupInfoDODO.getGroupId());
            dstUser.setUserId(sourceUserInfoDO.getUserId());
            dstUser.setUserName(sourceUserInfoDO.getUserName());
            dstUser.setUserInfo(sourceUserInfoDO.getUserInfo());
            dstUser.setFaceNumber(sourceUserInfoDO.getFaceNumber());
            userInfoMapper.insertGetId(dstUser);
            for (FaceDO faceDO : srcFaceDODOS) {
                FaceDO insertFaceDODO = new FaceDO();
                insertFaceDODO.setId(IDUtil.getRandomId());
                insertFaceDODO.setAppId(faceDO.getAppId());
                insertFaceDODO.setGid(dstGroupInfoDODO.getId());
                insertFaceDODO.setGroupId(dstGroupInfoDODO.getGroupId());
                insertFaceDODO.setUid(dstUser.getId());
                insertFaceDODO.setUserId(dstUser.getUserId());
                insertFaceDODO.setImagePath(faceDO.getImagePath());
                insertFaceDODO.setFeatures(faceDO.getFeatures());
                insertList.add(insertFaceDODO);
            }
        } else {
            userNumber = 0;
            // 存在同名用户的情况
            List<FaceDO> dstFaceDODOS = this.queryFace(appId, dstGroupId, userId);
            //过滤相同照片
            Set<String> srcImages = srcFaceDODOS.stream().map(FaceDO::getImagePath).collect(Collectors.toSet());
            Set<String> dstImages = dstFaceDODOS.stream().map(FaceDO::getImagePath).collect(Collectors.toSet());
            srcImages.removeAll(dstImages);
            //目标用户组的资料已和源用户组的资料一致
            if (CollectionUtils.isEmpty(srcFaceDODOS)) {
                return;
            }
            faceNumber = srcFaceDODOS.size();
            for (FaceDO faceDO : srcFaceDODOS) {
                //人脸不在已去重的范围内，跳过
                if (!srcImages.contains(faceDO.getImagePath())) {
                    continue;
                }
                FaceDO newFaceDODO = new FaceDO();
                newFaceDODO.setId(IDUtil.getRandomId());
                newFaceDODO.setAppId(faceDO.getAppId());
                newFaceDODO.setGid(dstGroupInfoDODO.getId());
                newFaceDODO.setGroupId(dstGroupInfoDODO.getGroupId());
                newFaceDODO.setUid(targetUserInfoDO.getId());
                newFaceDODO.setUserId(targetUserInfoDO.getUserId());
                newFaceDODO.setFeatures(faceDO.getFeatures());
                newFaceDODO.setImagePath(faceDO.getImagePath());
                insertList.add(newFaceDODO);
            }
        }
        //note 缓存中向目标用户组插入新的人脸
        if (faceCacheHelper.addBatch(insertList) == null) {
            log.info("[人脸新增向量失败],参数{}", "AppId:" + receive.getAppId() + "GroupId" + receive.getDstGroupId() + "userId" + receive.getUserId());
            throw ApiReturnErrorCode.CACHE_INSERT_ERROR.toException();
        }
        try {
            faceMapper.insertBatch(insertList);
        } catch (Exception e) {
            e.printStackTrace();
            throw ApiReturnErrorCode.DB_INSERT_ERROR.toException("[用户复制]", "AppId:" + receive.getAppId() + "GroupId" + receive.getDstGroupId() + "userId" + receive.getUserId());
        }
        //存在同名用户：组下用户数+0，人脸数+新增数
        publisher.publishEvent(new UserCopyEvent(appId, dstGroupId, userId, faceNumber, userNumber));
    }

    /**
     * 获取人脸库中的人脸信息
     */
    private List<FaceDO> queryFace(Long appId, String groupId, String userId) {
        FaceDO faceDODOQuery = new FaceDO();
        faceDODOQuery.setAppId(appId);
        faceDODOQuery.setGroupId(groupId);
        faceDODOQuery.setUserId(userId);
        PageInfo<FaceDO> allFace = PageHelper.offsetPage(faceDODOQuery.getStartIndex(), faceDODOQuery.getLength())
                .setOrderBy("create_time desc")
                .doSelectPageInfo(
                        () -> faceMapper.select(faceDODOQuery));
        return allFace.getList();
    }

    /**
     * 删除用户
     */
    @Override
    public void delete(NLBackend.BackendAllRequest receive) {
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
                throw new EntityExistsException("user_id doesn't exist !");
            }

            //note 缓存中删除用户的所有人脸
            List<Long> faceIdList = faceMapper.selectIdByUserId(userInfoDO.getUserId());
            if (faceCacheHelper.deleteBatch(query.getAppId(),faceIdList) < 0) {
                log.info("[人脸删除向量失败],参数{}", "AppId:" + receive.getAppId() + "GroupId" + receive.getDstGroupId() + "userId" + receive.getUserId());
                throw ApiReturnErrorCode.CACHE_DELETE_ERROR.toException("[删除用户]", "AppId:" + receive.getAppId() + "GroupId" + receive.getDstGroupId() + "userId" + receive.getUserId());
            }
            //物理删除用户及人脸
            int userCount;
            try {
                userCount = userInfoMapper.delete(query);
            } catch (Exception e) {
                e.printStackTrace();
                throw ApiReturnErrorCode.DB_UPDATE_ERROR.toException("[删除用户]", "AppId:" + receive.getAppId() + "GroupId" + receive.getDstGroupId() + "userId" + receive.getUserId());
            }
            FaceDO faceDO = new FaceDO();
            faceDO.setGroupId(group);
            faceDO.setUserId(userInfoDO.getUserId());
            faceDO.setAppId(receive.getAppId());
            try {
                faceMapper.delete(faceDO);
            } catch (Exception e) {
                e.printStackTrace();
                throw ApiReturnErrorCode.DB_UPDATE_ERROR.toException("[删除用户]", "AppId:" + receive.getAppId() + "GroupId" + receive.getDstGroupId() + "userId" + receive.getUserId());
            }

            publisher.publishEvent(new UserDeleteEvent(query.getAppId(), query.getGroupId(), query.getUserId(), userInfoDO.getFaceNumber(), userCount));

        }
    }

    /**
     * 注意：user_id只是在一个用户组里是唯一的，也就是说一个app下的不用的用户组中可以存在相同的user_id。
     * 换句话说user_id并不能唯一表示某个app下的某个user。
     */
    @Override
    public List<UserInfoDO> getInfo(NLBackend.BackendAllRequest receive) {
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
    private List<String> getGroupList(String userId, Long appId) {
        //获取用户所属的组列表
        List<String> groupIdList = userInfoMapper.getGroupIdByUserId(userId, appId);
        //仅返回有效状态的组id信息
        List<String> result = new ArrayList<>();
        if (!CollectionUtils.isEmpty(groupIdList)) {
            GroupInfoDO groupInfoDO = new GroupInfoDO();
            for (String groupId : groupIdList) {
                groupInfoDO.setAppId(appId);
                groupInfoDO.setGroupId(groupId);
                groupInfoDO.setIsDelete(StatusConstants.NOT_DELETE);
                if (groupInfoMapper.selectCount(groupInfoDO) > 0) {
                    result.add(groupId);
                }
            }
        }
        return result;
    }
}
