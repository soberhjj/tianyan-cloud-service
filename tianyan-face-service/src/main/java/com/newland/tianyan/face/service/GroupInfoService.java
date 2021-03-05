package com.newland.tianyan.face.service;


import com.github.pagehelper.PageInfo;
import com.newland.tianyan.common.exception.BaseException;
import com.newland.tianyan.common.utils.message.NLBackend;
import com.newland.tianyan.face.domain.entity.GroupInfoDO;

/**
 * @Author: huangJunJie  2020-11-02 14:04
 */
public interface GroupInfoService {

    void create(NLBackend.BackendAllRequest receive) throws BaseException;

    PageInfo<GroupInfoDO> getList(NLBackend.BackendAllRequest receive) throws BaseException;

    void delete(NLBackend.BackendAllRequest receive) throws BaseException;
}
