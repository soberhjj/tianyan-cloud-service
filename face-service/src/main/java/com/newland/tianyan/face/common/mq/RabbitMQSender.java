package com.newland.tianyan.face.common.mq;

import com.newland.tianyan.face.common.exception.FaceServiceErrorEnum;
import com.newland.tianyan.face.common.exception.FaceServiceException;
import org.springframework.amqp.AmqpException;
import org.springframework.amqp.core.AmqpTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * @author: RojiaHuang
 * @description:
 * @date: 2021/2/5
 */
@Component
public class RabbitMQSender {

    @Autowired
    private AmqpTemplate rabbitTemplate;

    public byte[] send(String routingKey, Object message) throws FaceServiceException {
        Object receive = null;
        try {
            receive = this.rabbitTemplate.convertSendAndReceive(routingKey, message);
        } catch (AmqpException exception) {
            exception.printStackTrace();
        }
        if (receive == null) {
            throw FaceServiceErrorEnum.RABBIT_MQ_RETURN_NONE.toException();
        }
        return (byte[]) receive;
    }
}
