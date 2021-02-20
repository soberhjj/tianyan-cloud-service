package com.newland.tianyan.face.service.impl;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.newland.tianyan.common.utils.generator.IDUtil;
import com.newland.tianyan.common.utils.message.NLBackend;
import com.newland.tianyan.common.utils.utils.ProtobufUtils;
import com.newland.tianyan.face.service.cache.ICacheHelper;
import com.newland.tianyan.face.constant.StatusConstants;
import com.newland.tianyan.face.dao.FaceMapper;
import com.newland.tianyan.face.dao.GroupInfoMapper;
import com.newland.tianyan.face.dao.UserInfoMapper;
import com.newland.tianyan.face.entity.Face;
import com.newland.tianyan.face.entity.GroupInfo;
import com.newland.tianyan.face.entity.UserInfo;
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
    private ICacheHelper<Face> faceCacheHelper;

    @Override
    public PageInfo<UserInfo> getList(NLBackend.BackendAllRequest receive) {
        UserInfo query = ProtobufUtils.parseTo(receive, UserInfo.class);

        //过滤掉用户组不合法的请求
        GroupInfo groupInfo = new GroupInfo();
        groupInfo.setAppId(query.getAppId());
        groupInfo.setGroupId(query.getGroupId());
        groupInfo.setIsDelete(StatusConstants.NOT_DELETE);
        if (groupInfoMapper.selectCount(groupInfo) <= 0) {
            return new PageInfo<>(new ArrayList<>());
        }

        return PageHelper.offsetPage(query.getStartIndex(), query.getLength())
                .doSelectPageInfo(
                        () -> {
                            Example example = new Example(UserInfo.class);
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
        UserInfo queryUser = ProtobufUtils.parseTo(receive, UserInfo.class);
        Long appId = receive.getAppId();
        String userId = receive.getUserId();
        String srcGroupId = receive.getSrcGroupId();
        String dstGroupId = receive.getDstGroupId();

        // 源用户组与目标用户组的状态有效性检查
        GroupInfo srcGroupInfo = new GroupInfo();
        srcGroupInfo.setAppId(queryUser.getAppId());
        srcGroupInfo.setGroupId(srcGroupId);
        srcGroupInfo.setIsDelete(StatusConstants.NOT_DELETE);
        boolean sourceInvalid = groupInfoMapper.selectCount(srcGroupInfo) > 0;
        if (!sourceInvalid) {
            throw new EntityExistsException("group_id " + srcGroupId + " doesn't exist!");
        }
        GroupInfo dstGroupInfo = new GroupInfo();
        dstGroupInfo.setAppId(queryUser.getAppId());
        dstGroupInfo.setGroupId(dstGroupId);
        dstGroupInfo.setIsDelete(StatusConstants.NOT_DELETE);
        dstGroupInfo = groupInfoMapper.selectOne(dstGroupInfo);
        boolean targetInvalid = dstGroupInfo != null;
        if (!targetInvalid) {
            throw new EntityExistsException("group_id " + dstGroupId + " doesn't exist!");
        }

        //查询源用户组中是否存在当前用户
        queryUser.setGroupId(srcGroupId);
        UserInfo sourceUserInfo = userInfoMapper.selectOne(queryUser);
        if (sourceUserInfo == null) {
            throw new EntityExistsException(userId + " doesn't exist in " + srcGroupId + "!");
        }
        //待复制的源用户的人脸资料
        List<Face> srcFaces = this.queryFace(appId, srcGroupId, userId);
        if (CollectionUtils.isEmpty(srcFaces)) {
            throw new EntityExistsException(userId + " doesn't has faces in " + srcGroupId + "!");
        }
        List<Face> insertList = new ArrayList<>(srcFaces.size());
        //查询目标用户组中是否存在当前用户
        queryUser.setGroupId(dstGroupId);
        UserInfo targetUserInfo = userInfoMapper.selectOne(queryUser);
        int faceNumber, userNumber;
        //不存在同名用户,直接新建
        if (targetUserInfo == null) {
            userNumber = 1;
            faceNumber = srcFaces.size();
            //新建目标用户组中的用户
            UserInfo dstUser = new UserInfo();
            dstUser.setAppId(sourceUserInfo.getAppId());
            dstUser.setGid(dstGroupInfo.getId());
            dstUser.setGroupId(dstGroupInfo.getGroupId());
            dstUser.setUserId(sourceUserInfo.getUserId());
            dstUser.setUserName(sourceUserInfo.getUserName());
            dstUser.setUserInfo(sourceUserInfo.getUserInfo());
            dstUser.setFaceNumber(sourceUserInfo.getFaceNumber());
            userInfoMapper.insertGetId(dstUser);
            for (Face face : srcFaces) {
                Face insertFace = new Face();
                insertFace.setId(IDUtil.getRandomId());
                insertFace.setAppId(face.getAppId());
                insertFace.setGid(dstGroupInfo.getId());
                insertFace.setGroupId(dstGroupInfo.getGroupId());
                insertFace.setUid(dstUser.getId());
                insertFace.setUserId(dstUser.getUserId());
                insertFace.setImagePath(face.getImagePath());
                insertFace.setFeatures(face.getFeatures());
                insertList.add(insertFace);
            }
        } else {
            userNumber = 0;
            // 存在同名用户的情况
            List<Face> dstFaces = this.queryFace(appId, dstGroupId, userId);
            //过滤相同照片
            Set<String> srcImages = srcFaces.stream().map(Face::getImagePath).collect(Collectors.toSet());
            Set<String> dstImages = dstFaces.stream().map(Face::getImagePath).collect(Collectors.toSet());
            srcImages.removeAll(dstImages);
            //目标用户组的资料已和源用户组的资料一致
            if (CollectionUtils.isEmpty(srcFaces)) {
                return;
            }
            faceNumber = srcFaces.size();
            for (Face face : srcFaces) {
                //人脸不在已去重的范围内，跳过
                if (!srcImages.contains(face.getImagePath())) {
                    continue;
                }
                Face newFace = new Face();
                newFace.setId(IDUtil.getRandomId());
                newFace.setAppId(face.getAppId());
                newFace.setGid(dstGroupInfo.getId());
                newFace.setGroupId(dstGroupInfo.getGroupId());
                newFace.setUid(targetUserInfo.getId());
                newFace.setUserId(targetUserInfo.getUserId());
                newFace.setFeatures(face.getFeatures());
                newFace.setImagePath(face.getImagePath());
                insertList.add(newFace);
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
    private List<Face> queryFace(Long appId, String groupId, String userId) {
        Face faceQuery = new Face();
        faceQuery.setAppId(appId);
        faceQuery.setGroupId(groupId);
        faceQuery.setUserId(userId);
        PageInfo<Face> allFace = PageHelper.offsetPage(faceQuery.getStartIndex(), faceQuery.getLength())
                .setOrderBy("create_time desc")
                .doSelectPageInfo(
                        () -> faceMapper.select(faceQuery));
        return allFace.getList();
    }

    /**
     * 删除用户
     */
    @Override
    public void delete(NLBackend.BackendAllRequest receive) {
        UserInfo query = ProtobufUtils.parseTo(receive, UserInfo.class);

        String[] groups = query.getGroupId().split(",");
        for (String group : groups) {
            query.setGroupId(group);

            UserInfo userInfo = new UserInfo();
            userInfo.setUserId(query.getUserId());
            userInfo.setAppId(query.getAppId());
            userInfo.setGroupId(query.getGroupId());
            userInfo = userInfoMapper.selectOne(userInfo);
            if (userInfo == null) {
                throw new EntityExistsException("user_id doesn't exist !");
            }

            //note 缓存中删除用户的所有人脸
            List<Long> faceIdList = faceMapper.selectIdByUserId(userInfo.getUserId());
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
            Face face = new Face();
            face.setGroupId(group);
            face.setUserId(userInfo.getUserId());
            face.setAppId(receive.getAppId());
            try {
                faceMapper.delete(face);
            } catch (Exception e) {
                e.printStackTrace();
                throw ApiReturnErrorCode.DB_UPDATE_ERROR.toException("[删除用户]", "AppId:" + receive.getAppId() + "GroupId" + receive.getDstGroupId() + "userId" + receive.getUserId());
            }

            publisher.publishEvent(new UserDeleteEvent(query.getAppId(), query.getGroupId(), query.getUserId(), userInfo.getFaceNumber(), userCount));

        }
    }

    /**
     * 注意：user_id只是在一个用户组里是唯一的，也就是说一个app下的不用的用户组中可以存在相同的user_id。
     * 换句话说user_id并不能唯一表示某个app下的某个user。
     */
    @Override
    public List<UserInfo> getInfo(NLBackend.BackendAllRequest receive) {
        UserInfo userQuery = ProtobufUtils.parseTo(receive, UserInfo.class);
        //如果传入的参数中没有传入用户组的话，那么就根据传入的app_id去获取该app的所有用户组。
        List<String> groupIds = this.getGroupList(userQuery.getUserId(), userQuery.getAppId());
        if (CollectionUtils.isEmpty(groupIds)) {
            return new ArrayList<>();
        }
        List<UserInfo> userList = new ArrayList<>();
        for (String groupId : groupIds) {
            //user表索引userId、groupId
            userQuery.setUserId(userQuery.getUserId());
            userQuery.setGroupId(groupId);

            //接下来就是去user_info表中根据app_id和group_id和user_id查询用户信息
            UserInfo userInfo = userInfoMapper.selectOne(userQuery);
            if (userInfo != null) {
                userList.add(userInfo);
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
            GroupInfo groupInfo = new GroupInfo();
            for (String groupId : groupIdList) {
                groupInfo.setAppId(appId);
                groupInfo.setGroupId(groupId);
                groupInfo.setIsDelete(StatusConstants.NOT_DELETE);
                if (groupInfoMapper.selectCount(groupInfo) > 0) {
                    result.add(groupId);
                }
            }
        }
        return result;
    }
}
