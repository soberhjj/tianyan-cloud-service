package com.newland.tianyan.face.service;

import com.github.pagehelper.PageInfo;
import com.newland.tianyan.face.common.exception.FaceServiceException;
import com.newland.tianyan.face.domain.GroupInfo;
import com.newland.tianyan.face.dto.group.BackendFacesetGroupAddRequest;
import com.newland.tianyan.face.dto.group.BackendFacesetGroupDeleteRequest;
import com.newland.tianyan.face.dto.group.BackendFacesetGroupGetListRequest;

/**
 * @author: RojiaHuang
 * @description:
 * @date: 2021/2/5
 */
public interface GroupInfoService {

    void create(BackendFacesetGroupAddRequest receive) throws FaceServiceException;

    PageInfo<GroupInfo> getList(BackendFacesetGroupGetListRequest receive) throws FaceServiceException;

    void delete(BackendFacesetGroupDeleteRequest receive) throws FaceServiceException;
}
