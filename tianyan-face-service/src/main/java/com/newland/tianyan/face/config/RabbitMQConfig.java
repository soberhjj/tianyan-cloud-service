package com.newland.tianyan.face.config;

import org.springframework.amqp.core.Queue;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

@Component
public class RabbitMQConfig {

    //TODO: remove magic number
    @Bean
    public Queue queue() {
        return new Queue("faceDetectQueue");
    }
}