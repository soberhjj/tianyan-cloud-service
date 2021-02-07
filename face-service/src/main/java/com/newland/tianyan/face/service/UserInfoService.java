package com.newland.tianyan.face.service;

import com.github.pagehelper.PageInfo;
import com.newland.tianyan.face.domain.UserInfo;
import com.newland.tianyan.face.dto.user.BackendFacesetUserCopyRequest;
import com.newland.tianyan.face.dto.user.BackendFacesetUserDeleteRequest;
import com.newland.tianyan.face.dto.user.BackendFacesetUserGetListRequest;
import com.newland.tianyan.face.dto.user.BackendFacesetUserMessageRequest;

import java.util.List;

/**
 * @author: RojiaHuang
 * @description:
 * @date: 2021/2/5
 */
public interface UserInfoService {
    PageInfo<UserInfo> getList(BackendFacesetUserGetListRequest receive);

    void copy(BackendFacesetUserCopyRequest receive);

    void delete(BackendFacesetUserDeleteRequest receive);

    List<UserInfo> getInfo(BackendFacesetUserMessageRequest receive);

}
