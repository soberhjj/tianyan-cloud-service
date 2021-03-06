package com.newland.tianyan.face.service;


import com.newland.tianya.commons.base.model.proto.NLBackend;
import com.newland.tianyan.face.domain.entity.FaceDO;

import java.io.IOException;
import java.util.List;

/**
 * @Author: huangJunJie  2020-11-04 09:14
 */
public interface FacesetUserFaceService {

    FaceDO create(NLBackend.BackendAllRequest receive) throws IOException;

    List<FaceDO> getList(NLBackend.BackendAllRequest receive) throws IOException;

    void delete(NLBackend.BackendAllRequest receive);
}
