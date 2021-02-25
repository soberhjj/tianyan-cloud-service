package com.newland.tianyan.face.service;


import com.github.pagehelper.PageInfo;
import com.newland.tianyan.common.utils.message.NLBackend;
import com.newland.tianyan.face.domain.entity.UserInfoDO;
import com.newland.tianyan.face.exception.ApiException;

import java.util.List;

/**
 * @Author: huangJunJie  2020-11-07 09:17
 */
public interface FacesetUserService {

    PageInfo<UserInfoDO> getList(NLBackend.BackendAllRequest receive);

    void copy(NLBackend.BackendAllRequest receive)throws ApiException;

    void delete(NLBackend.BackendAllRequest receive);

    List<UserInfoDO> getInfo(NLBackend.BackendAllRequest receive);

}
