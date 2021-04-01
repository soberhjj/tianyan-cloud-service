package com.newland.tianyan.face.config;

import com.newland.tianyan.face.mq.RabbitMqQueueName;
import org.springframework.amqp.core.Queue;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

@Component
public class RabbitMQConfig {

    /**
     * 人脸检测队列,一般与下方feature队列成队出现
     */
    @Bean
    public Queue detectQueue() {
        return new Queue(RabbitMqQueueName.FACE_DETECT_QUEUE);
    }

    @Bean
    public Queue detectV18Queue() {
        return new Queue(RabbitMqQueueName.FACE_DETECT_QUEUE);
    }

    @Bean
    public Queue detectV20Queue() {
        return new Queue(RabbitMqQueueName.FACE_DETECT_QUEUE);
    }

    @Bean
    public Queue detectV20OldQueue() {
        return new Queue(RabbitMqQueueName.FACE_DETECT_QUEUE);
    }

    @Bean
    public Queue detectV34Queue() {
        return new Queue(RabbitMqQueueName.FACE_DETECT_QUEUE);
    }

    @Bean
    public Queue detectV36Queue() {
        return new Queue(RabbitMqQueueName.FACE_DETECT_QUEUE);
    }

    /**
     * 人脸feature队列
     */
    @Bean
    public Queue featureQueue() {
        return new Queue(RabbitMqQueueName.FACE_FEATURE_QUEUE);
    }

    @Bean
    public Queue featureV18Queue() {
        return new Queue(RabbitMqQueueName.FACE_FEATURE_QUEUE_V18);
    }

    @Bean
    public Queue featureV20Queue() {
        return new Queue(RabbitMqQueueName.FACE_FEATURE_QUEUE_V20);
    }

    @Bean
    public Queue featureV20OldQueue() {
        return new Queue(RabbitMqQueueName.FACE_FEATURE_QUEUE_V20_OLD);
    }

    @Bean
    public Queue featureV34Queue() {
        return new Queue(RabbitMqQueueName.FACE_FEATURE_QUEUE_V34);
    }

    @Bean
    public Queue featureV36Queue() {
        return new Queue(RabbitMqQueueName.FACE_FEATURE_QUEUE_V36);
    }
}
