package com.newland.tianyan.face.service;

import com.newland.tianyan.face.domain.FaceInfo;
import com.newland.tianyan.face.dto.userFace.BackendFacesetFaceAddRequest;
import com.newland.tianyan.face.dto.userFace.BackendFacesetFaceDeleteRequest;
import com.newland.tianyan.face.dto.userFace.BackendFacesetUserFaceGetListRequest;

import java.util.List;

/**
 * @author: RojiaHuang
 * @description:
 * @date: 2021/2/5
 */
public interface UserFaceInfoService {

    FaceInfo create(BackendFacesetFaceAddRequest receive);

    List<FaceInfo> getList(BackendFacesetUserFaceGetListRequest receive);

    void delete(BackendFacesetFaceDeleteRequest receive);
}
