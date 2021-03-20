package com.newland.tianyan.face.service;


import com.github.pagehelper.PageInfo;
import com.newland.tianya.commons.base.exception.BaseException;
import com.newland.tianyan.common.utils.message.NLBackend;
import com.newland.tianyan.face.domain.entity.AppInfoDO;

/**
 * @description: 用户接口
 **/
public interface AppInfoService {

     void insert(NLBackend.BackendAllRequest receive) throws BaseException;

     AppInfoDO getInfo(NLBackend.BackendAllRequest receive) throws BaseException;

     PageInfo<AppInfoDO> getList(NLBackend.BackendAllRequest receive) throws BaseException;

     void update(NLBackend.BackendAllRequest receive) throws BaseException;

     void delete(NLBackend.BackendAllRequest receive) throws BaseException;
}
