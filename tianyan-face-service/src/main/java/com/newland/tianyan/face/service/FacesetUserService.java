package com.newland.tianyan.face.service;


import com.github.pagehelper.PageInfo;
import com.newland.tianya.commons.base.exception.BaseException;
import com.newland.tianya.commons.base.model.proto.NLBackend;
import com.newland.tianyan.face.domain.entity.UserInfoDO;

import java.util.List;

/**
 * @Author: huangJunJie  2020-11-07 09:17
 */
public interface FacesetUserService {

    PageInfo<UserInfoDO> getList(NLBackend.BackendAllRequest receive) throws BaseException;

    void copy(NLBackend.BackendAllRequest receive) throws BaseException;

    void delete(NLBackend.BackendAllRequest receive) throws BaseException;

    List<UserInfoDO> getInfo(NLBackend.BackendAllRequest receive) throws BaseException;

}
