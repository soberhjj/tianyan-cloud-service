package com.newland.tianyan.face.service;


import com.github.pagehelper.PageInfo;
import com.newland.tianyan.common.exception.CommonException;
import com.newland.tianyan.common.utils.message.NLBackend;
import com.newland.tianyan.face.domain.entity.AppInfoDO;

/**
 * @description: 用户接口
 **/
public interface AppInfoService {

     void insert(NLBackend.BackendAllRequest receive) throws CommonException;

     AppInfoDO getInfo(NLBackend.BackendAllRequest receive) throws CommonException;

     PageInfo<AppInfoDO> getList(NLBackend.BackendAllRequest receive) throws CommonException;

     void update(NLBackend.BackendAllRequest receive) throws CommonException;

     void delete(NLBackend.BackendAllRequest receive) throws CommonException;
}
