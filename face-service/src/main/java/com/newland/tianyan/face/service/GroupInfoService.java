package com.newland.tianyan.face.service;


import com.github.pagehelper.PageInfo;
import com.newland.tianyan.common.utils.message.NLBackend;
import com.newland.tianyan.face.domain.entity.GroupInfoDO;
import com.newland.tianyan.face.exception.ApiException;

/**
 * @Author: huangJunJie  2020-11-02 14:04
 */
public interface GroupInfoService {

    void create(NLBackend.BackendAllRequest receive) throws ApiException;

    PageInfo<GroupInfoDO> getList(NLBackend.BackendAllRequest receive) throws ApiException;

    void delete(NLBackend.BackendAllRequest receive) throws ApiException;
}
