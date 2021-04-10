package com.newland.tianyan.face.service;


import com.github.pagehelper.PageInfo;
import com.newland.tianya.commons.base.exception.BaseException;
import com.newland.tianya.commons.base.model.proto.NLBackend;
import com.newland.tianyan.face.domain.entity.GroupInfoDO;

import java.util.List;
import java.util.Set;

/**
 * @Author: huangJunJie  2020-11-02 14:04
 */
public interface GroupInfoService {

    void create(NLBackend.BackendAllRequest receive) throws BaseException;

    PageInfo<GroupInfoDO> getList(NLBackend.BackendAllRequest receive) throws BaseException;

    void delete(NLBackend.BackendAllRequest receive) throws BaseException;

    List<GroupInfoDO> queryBatch(Long appId, Set<String> groupIdList);

}
