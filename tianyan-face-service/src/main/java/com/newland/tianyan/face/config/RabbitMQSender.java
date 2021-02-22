package com.newland.tianyan.face.config;


import com.newland.tianyan.face.exception.ApiException;
import com.newland.tianyan.face.exception.ApiReturnErrorCode;
import org.springframework.amqp.AmqpException;
import org.springframework.amqp.core.AmqpTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class RabbitMQSender {

    @Autowired
    private AmqpTemplate rabbitTemplate;

    public byte[] send(String routingKey, Object message) throws ApiException {
        Object receive = null;
        try {
            receive = this.rabbitTemplate.convertSendAndReceive(routingKey, message);
        } catch (AmqpException exception) {
            exception.printStackTrace();
        }
        if (receive == null) {
            throw ApiReturnErrorCode.RABBIT_MQ_RETURN_NONE.toException();
        }
        return (byte[]) receive;
    }

}
