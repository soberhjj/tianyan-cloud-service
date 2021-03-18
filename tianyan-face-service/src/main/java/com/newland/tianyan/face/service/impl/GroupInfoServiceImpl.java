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
import com.newland.tianyan.face.domain.entity.FaceDO;
import com.newland.tianyan.face.domain.entity.GroupInfoDO;
import com.newland.tianyan.face.event.group.AbstractGroupCreateEvent;
import com.newland.tianyan.face.event.group.AbstractGroupDeleteEvent;
import com.newland.tianyan.face.service.GroupInfoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import tk.mybatis.mapper.entity.Example;

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
    private VectorSearchServiceImpl<FaceDO> faceCacheHelper;


    @Override
    public void create(NLBackend.BackendAllRequest receive) throws BaseException {
        GroupInfoDO groupInfoDO = ProtobufUtils.parseTo(receive, GroupInfoDO.class);
        //判断用户组是否存在
        groupInfoDO.setIsDelete(EntityStatusConstants.NOT_DELETE);
        if (groupInfoMapper.selectCount(groupInfoDO) > 0) {
            throw BusinessErrorEnums.GROUP_ALREADY_EXISTS.toException(receive.getGroupId());
        }

        //添加用户组
        groupInfoDO.setIsDelete(EntityStatusConstants.NOT_DELETE);
        groupInfoDO.setFaceNumber(0);
        groupInfoDO.setUserNumber(0);
        if (groupInfoMapper.insertSelective(groupInfoDO) < 0) {
            throw SystemErrorEnums.DB_INSERT_ERROR.toException(JsonUtils.toJson(groupInfoDO));
        }

        //发布事件。由于新增了用户组，所以要在app_info表中将该用户组对应的app的那条记录中的group_number值加1
        publisher.publishEvent(new AbstractGroupCreateEvent(receive.getAppId(), receive.getGroupId()));

    }

    @Override
    public PageInfo<GroupInfoDO> getList(NLBackend.BackendAllRequest receive) throws BaseException {
        GroupInfoDO query = ProtobufUtils.parseTo(receive, GroupInfoDO.class);
        return PageHelper.offsetPage(query.getStartIndex(), query.getLength())
                .doSelectPageInfo(
                        () -> {
                            Example example = new Example(GroupInfoDO.class);
                            Example.Criteria criteria = example.createCriteria();

                            criteria.andEqualTo("appId", query.getAppId());
                            criteria.andEqualTo("isDelete", EntityStatusConstants.NOT_DELETE);
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
    public void delete(NLBackend.BackendAllRequest receive) throws BaseException {
        GroupInfoDO groupInfoDO = ProtobufUtils.parseTo(receive, GroupInfoDO.class);
        groupInfoDO.setIsDelete(EntityStatusConstants.NOT_DELETE);
        GroupInfoDO groupToDelete = groupInfoMapper.selectOne(groupInfoDO);
        if (groupToDelete == null) {
            throw BusinessErrorEnums.GROUP_NOT_FOUND.toException(receive.getGroupId());
        }

        //todo 删除该组所有用户的所有人脸-可以做个闲时的人脸删除
        List<Long> faceIdList = faceMapper.selectIdByGroupId(groupToDelete.getGroupId());
        faceCacheHelper.deleteBatch(groupInfoDO.getAppId(), faceIdList);

        //逻辑删除
        if (groupInfoMapper.updateToDelete(EntityStatusConstants.DELETE, groupToDelete.getId()) < 0) {
            throw SystemErrorEnums.DB_DELETE_ERROR.toException();
        }

        //发布事件。由于删除了用户组，所以要在app_info表中将该用户组对应的app的那条记录中的group_number值减1
        publisher.publishEvent(new AbstractGroupDeleteEvent(groupToDelete.getAppId(), groupToDelete.getGroupId()));
    }
}
