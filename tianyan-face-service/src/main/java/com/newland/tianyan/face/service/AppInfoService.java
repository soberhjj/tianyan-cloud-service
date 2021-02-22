package com.newland.tianyan.face.service;


import com.github.pagehelper.PageInfo;
import com.newland.tianyan.common.utils.message.NLBackend;
import com.newland.tianyan.face.domain.entity.AppInfoDO;

/**
 * @description: 用户接口
 **/
public interface AppInfoService {

     void insert(NLBackend.BackendAllRequest receive) ;

     AppInfoDO getInfo(NLBackend.BackendAllRequest receive) ;

     PageInfo<AppInfoDO> getList(NLBackend.BackendAllRequest receive) ;

     void update(NLBackend.BackendAllRequest receive) ;

     void delete(NLBackend.BackendAllRequest receive);
}
