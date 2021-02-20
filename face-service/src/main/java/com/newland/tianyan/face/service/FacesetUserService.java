package com.newland.tianyan.face.service;


import com.github.pagehelper.PageInfo;
import com.newland.tianyan.common.utils.message.NLBackend;
import com.newland.tianyan.face.entity.UserInfo;

import java.util.List;

/**
 * @Author: huangJunJie  2020-11-07 09:17
 */
public interface FacesetUserService {

    PageInfo<UserInfo> getList(NLBackend.BackendAllRequest receive);

    void copy(NLBackend.BackendAllRequest receive);

    void delete(NLBackend.BackendAllRequest receive);

    List<UserInfo> getInfo(NLBackend.BackendAllRequest receive);

}
