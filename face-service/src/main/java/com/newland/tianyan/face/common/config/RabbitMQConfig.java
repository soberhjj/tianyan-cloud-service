package com.newland.tianyan.face.common.config;

import com.newland.tianyan.face.common.mq.RabbitMqQueueName;
import org.springframework.amqp.core.Queue;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

@Component
public class RabbitMQConfig {

    @Bean
    public Queue queue() {
        return new Queue(RabbitMqQueueName.FACE_DETECT_QUEUE);
    }
}
