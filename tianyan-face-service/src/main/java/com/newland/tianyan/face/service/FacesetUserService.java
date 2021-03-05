package com.newland.tianyan.face.service;


import com.github.pagehelper.PageInfo;
import com.newland.tianyan.common.exception.CommonException;
import com.newland.tianyan.common.utils.message.NLBackend;
import com.newland.tianyan.face.domain.entity.UserInfoDO;

import java.util.List;

/**
 * @Author: huangJunJie  2020-11-07 09:17
 */
public interface FacesetUserService {

    PageInfo<UserInfoDO> getList(NLBackend.BackendAllRequest receive) throws CommonException;

    void copy(NLBackend.BackendAllRequest receive) throws CommonException;

    void delete(NLBackend.BackendAllRequest receive) throws CommonException;

    List<UserInfoDO> getInfo(NLBackend.BackendAllRequest receive) throws CommonException;

}
