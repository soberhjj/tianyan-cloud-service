package com.newland.tianyan.face.mq;

import com.googlecode.protobuf.format.JsonFormat;
import com.newland.tianya.commons.base.exception.BaseException;
import com.newland.tianya.commons.base.model.proto.NLFace;
import com.newland.tianya.commons.base.support.ExceptionSupport;
import com.newland.tianyan.face.constant.ExceptionEnum;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.UUID;

import static com.newland.tianya.commons.base.constants.GlobalExceptionEnum.BASE64_FORMAT_ILLEGAL;
import static com.newland.tianya.commons.base.constants.GlobalExceptionEnum.SYSTEM_ERROR;
import static com.newland.tianyan.face.constant.ExceptionEnum.PICTURE_HAS_NO_FACE;
import static com.newland.tianyan.face.constant.ExceptionEnum.RABBIT_MQ_RETURN_NONE;

/**
 * @author: RojiaHuang
 * @description:
 * @date: 2021/4/2
 */
@Service
public class MQMessageServiceImpl implements IMqMessageService {

    @Autowired
    private RabbitMQSender rabbitMqSender;

    @Override
    public NLFace.CloudFaceSendMessage amqpHelper(String fileName, int maxFaceNum, Integer taskType) throws BaseException {
        // get features
        String logId = UUID.randomUUID().toString();
        NLFace.CloudFaceAllRequest.Builder amqpRequest = NLFace.CloudFaceAllRequest.newBuilder();
        amqpRequest.setLogId(logId);
        amqpRequest.setTaskType(taskType);
        amqpRequest.setImage(fileName);
        amqpRequest.setMaxFaceNum(maxFaceNum);

        byte[] message = amqpRequest.build().toByteArray();
        String routingKey = this.getRoutingKey(taskType);
        String json = new String(rabbitMqSender.send(routingKey, message));

        NLFace.CloudFaceSendMessage.Builder result = NLFace.CloudFaceSendMessage.newBuilder();
        try {
            JsonFormat.merge(json, result);
        } catch (JsonFormat.ParseException e) {
            throw ExceptionSupport.toException(ExceptionEnum.PROTO_PARSE_ERROR, e);

        }
        NLFace.CloudFaceSendMessage build = result.build();
        if (!StringUtils.isEmpty(build.getErrorMsg())) {
            int errorCode = build.getErrorCode();
            //算法固定错误码6201、6402转化为本项目内部错误码
            switch (errorCode) {
                case 6201:
                    throw ExceptionSupport.toException(BASE64_FORMAT_ILLEGAL);
                case 6402:
                    throw ExceptionSupport.toException(PICTURE_HAS_NO_FACE);
                default:
                    throw ExceptionSupport.toException(RABBIT_MQ_RETURN_NONE);
            }
        }
        return build;
    }

    private String getRoutingKey(Integer taskType) {
        String routingKey;
        switch (taskType) {
            case 18:
                routingKey = RabbitMqQueueName.FACE_DETECT_QUEUE_V18;
                break;
            case -20:
                routingKey = RabbitMqQueueName.FACE_DETECT_QUEUE_V20_OLD;
                break;
            case 20:
                routingKey = RabbitMqQueueName.FACE_DETECT_QUEUE_V20;
                break;
            case 34:
                routingKey = RabbitMqQueueName.FACE_DETECT_QUEUE_V34;
                break;
            case 36:
                routingKey = RabbitMqQueueName.FACE_DETECT_QUEUE_V36;
                break;
            default:
                routingKey = RabbitMqQueueName.FACE_DETECT_QUEUE;
        }
        return routingKey;
    }

}
