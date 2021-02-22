package com.newland.tianyan.face.service;


import com.newland.tianyan.common.utils.message.NLBackend;
import com.newland.tianyan.face.domain.entity.FaceDO;

import java.util.List;

/**
 * @Author: huangJunJie  2020-11-04 09:14
 */
public interface FacesetUserFaceService {

    FaceDO create(NLBackend.BackendAllRequest receive);

//    NLFace.CloudFaceSendMessage amqpHelper(String fileName, int maxFaceNum, Integer taskType);
//
//    NLFace.CloudFaceSendMessage JSONSendHelper(String routingKey, byte[] msg);

    List<FaceDO> getList(NLBackend.BackendAllRequest receive);

    void delete(NLBackend.BackendAllRequest receive);
}
