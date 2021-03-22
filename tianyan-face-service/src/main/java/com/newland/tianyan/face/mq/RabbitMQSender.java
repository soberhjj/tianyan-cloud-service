package com.newland.tianyan.face.mq;


import com.newland.tianya.commons.base.exception.SysException;
import com.newland.tianyan.face.constant.ExceptionEnum;
import org.springframework.amqp.AmqpException;
import org.springframework.amqp.core.AmqpTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class RabbitMQSender {

    @Autowired
    private AmqpTemplate rabbitTemplate;

    public byte[] send(String routingKey, Object message) throws SysException {
        Object receive = null;
        try {
            receive = this.rabbitTemplate.convertSendAndReceive(routingKey, message);
        } catch (AmqpException exception) {
            exception.printStackTrace();
        }
        if (receive == null) {
            throw ExceptionEnum.RABBIT_MQ_RETURN_NONE.toException();
        }
        return (byte[]) receive;
    }

}
