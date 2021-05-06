package com.newland.tianyan.face.service.impl;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.newland.tianya.commons.base.exception.BaseException;
import com.newland.tianya.commons.base.model.proto.NLBackend;
import com.newland.tianya.commons.base.support.ExceptionSupport;
import com.newland.tianya.commons.base.utils.ProtobufUtils;
import com.newland.tianyan.face.constant.EntityStatusConstants;
import com.newland.tianyan.face.constant.ExceptionEnum;
import com.newland.tianyan.face.dao.FaceMapper;
import com.newland.tianyan.face.dao.GroupInfoMapper;
import com.newland.tianyan.face.domain.entity.FaceDO;
import com.newland.tianyan.face.domain.entity.GroupInfoDO;
import com.newland.tianyan.face.event.group.GroupCreateEvent;
import com.newland.tianyan.face.event.group.GroupDeleteEvent;
import com.newland.tianyan.face.service.GroupInfoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import tk.mybatis.mapper.entity.Example;

import java.util.List;
import java.util.Set;

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
            throw ExceptionSupport.toException(ExceptionEnum.GROUP_ALREADY_EXISTS, receive.getGroupId());
        }

        //添加用户组
        groupInfoDO.setIsDelete(EntityStatusConstants.NOT_DELETE);
        groupInfoDO.setFaceNumber(0);
        groupInfoDO.setUserNumber(0);
        groupInfoMapper.insertSelective(groupInfoDO);

        //发布事件。由于新增了用户组，所以要在app_info表中将该用户组对应的app的那条记录中的group_number值加1
        publisher.publishEvent(new GroupCreateEvent(receive.getAppId(), receive.getGroupId()));

    }

    @Override
    public PageInfo<GroupInfoDO> getList(NLBackend.BackendAllRequest receive) throws BaseException {
        GroupInfoDO query = ProtobufUtils.parseTo(receive, GroupInfoDO.class);
        return PageHelper.startPage(query.getStartIndex() , query.getLength())
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
            throw ExceptionSupport.toException(ExceptionEnum.GROUP_NOT_FOUND, receive.getGroupId());
        }

        //todo 删除该组所有用户的所有人脸-可以做个闲时的人脸删除
//        List<Long> faceIdList = faceMapper.selectIdByGroupId(groupToDelete.getGroupId());
//        faceCacheHelper.deleteBatch(groupInfoDO.getAppId(), faceIdList);

        //逻辑删除
        groupInfoMapper.updateToDelete(EntityStatusConstants.DELETE, groupToDelete.getId());

        //发布事件。由于删除了用户组，所以要在app_info表中将该用户组对应的app的那条记录中的group_number值减1
        publisher.publishEvent(new GroupDeleteEvent(groupToDelete.getAppId(), groupToDelete.getGroupId()));
    }

    @Override
    public List<GroupInfoDO> queryBatch(Long appId, Set<String> groupIdList) {

        List<GroupInfoDO> groupList = groupInfoMapper.queryBatch(appId, groupIdList);
        if (CollectionUtils.isEmpty(groupList)) {
            throw ExceptionSupport.toException(ExceptionEnum.GROUP_NOT_FOUND, groupIdList.toString());
        }
        return groupList;
    }

}
