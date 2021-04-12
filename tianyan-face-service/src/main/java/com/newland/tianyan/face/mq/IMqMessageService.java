package com.newland.tianyan.face.mq;

import com.newland.tianya.commons.base.exception.BaseException;
import com.newland.tianya.commons.base.model.proto.NLFace;

/**
 * @author: RojiaHuang
 * @description:
 * @date: 2021/4/2
 */
public interface IMqMessageService {

    NLFace.CloudFaceSendMessage amqpHelper(String fileName, int maxFaceNum, Integer taskType) throws BaseException;
}
