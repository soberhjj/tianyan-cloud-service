package com.newland.tianyan.face.service.impl;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.newland.tianyan.common.utils.exception.CommonException;
import com.newland.tianyan.common.utils.message.NLBackend;
import com.newland.tianyan.common.utils.utils.ProtobufUtils;
import com.newland.tianyan.face.service.cache.FaceCacheHelperImpl;
import com.newland.tianyan.face.constant.StatusConstants;
import com.newland.tianyan.face.dao.FaceMapper;
import com.newland.tianyan.face.dao.GroupInfoMapper;
import com.newland.tianyan.face.entity.Face;
import com.newland.tianyan.face.entity.GroupInfo;
import com.newland.tianyan.face.event.group.GroupCreateEvent;
import com.newland.tianyan.face.event.group.GroupDeleteEvent;
import com.newland.tianyan.face.exception.ApiReturnErrorCode;
import com.newland.tianyan.face.service.GroupInfoService;
import com.newland.tianyan.common.utils.utils.JsonUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import tk.mybatis.mapper.entity.Example;

import javax.persistence.EntityExistsException;
import java.util.List;

/**
 * @Author: huangJunJie  2020-11-02 14:11
 */
@Service
public class GroupInfoServiceImpl implements GroupInfoService {

    @Autowired
    private ApplicationEventPublisher publisher;
    @Autowired
    private GroupInfoMapper groupInfoMapper;
    @Autowired
    private FaceMapper faceMapper;
    @Autowired
    private FaceCacheHelperImpl<Face> faceCacheHelper;


    @Override
    public void create(NLBackend.BackendAllRequest receive) {
        GroupInfo groupInfo = ProtobufUtils.parseTo(receive, GroupInfo.class);
        //判断用户组是否存在
        groupInfo.setIsDelete(StatusConstants.NOT_DELETE);
        if (groupInfoMapper.selectCount(groupInfo) > 0) {
            throw new EntityExistsException("same group_id exist in app:" + receive.getGroupId());
        }

        //添加用户组
        groupInfo.setIsDelete(StatusConstants.NOT_DELETE);
        groupInfo.setFaceNumber(0);
        groupInfo.setUserNumber(0);
        groupInfoMapper.insertSelective(groupInfo);

        //发布事件。由于新增了用户组，所以要在app_info表中将该用户组对应的app的那条记录中的group_number值加1
        publisher.publishEvent(new GroupCreateEvent(receive.getAppId(), receive.getGroupId()));

    }

    @Override
    public PageInfo<GroupInfo> getList(NLBackend.BackendAllRequest receive) {
        GroupInfo query = ProtobufUtils.parseTo(receive, GroupInfo.class);
        return PageHelper.offsetPage(query.getStartIndex(), query.getLength())
                .doSelectPageInfo(
                        () -> {
                            Example example = new Example(GroupInfo.class);
                            Example.Criteria criteria = example.createCriteria();

                            criteria.andEqualTo("appId", query.getAppId());
                            criteria.andEqualTo("isDelete", StatusConstants.NOT_DELETE);
                            // dynamic sql
                            if (query.getGroupId() != null) {
                                criteria.andLike("groupId", "%" + query.getGroupId() + "%");
                            }

                            // execute select
                            groupInfoMapper.selectByExample(example);
                        }
                );
    }

    /**
     * 删除用户组
     */
    @Override
    public void delete(NLBackend.BackendAllRequest receive) {
        GroupInfo groupInfo = ProtobufUtils.parseTo(receive, GroupInfo.class);
        groupInfo.setIsDelete(StatusConstants.NOT_DELETE);
        GroupInfo groupToDelete = groupInfoMapper.selectOne(groupInfo);
        if (groupToDelete == null) {
            throw new EntityExistsException("group_id not exist in app:" + receive.getGroupId());
        }

        //todo 删除该组所有用户的所有人脸-可以做个闲时的人脸删除
        List<Long> faceIdList = faceMapper.selectIdByGroupId(groupToDelete.getGroupId());
        if (faceCacheHelper.deleteBatch(groupInfo.getAppId(),faceIdList) < 0) {
            throw ApiReturnErrorCode.CACHE_DELETE_ERROR.toException("[人脸管理]-移除用户组", "faceId:" + JsonUtils.toJson(faceIdList));
        }

        //逻辑删除
        try {
            groupInfoMapper.updateToDelete(StatusConstants.DELETE, groupToDelete.getId());
        } catch (Exception e) {
            throw new CommonException(6100, "invalid param");
        }

        //发布事件。由于删除了用户组，所以要在app_info表中将该用户组对应的app的那条记录中的group_number值减1
        publisher.publishEvent(new GroupDeleteEvent(groupToDelete.getAppId(), groupToDelete.getGroupId()));
    }
}
