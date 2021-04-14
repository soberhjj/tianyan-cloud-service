package com.newland.tianyan.face.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.concurrent.Executor;
import java.util.concurrent.ThreadPoolExecutor;

/**
 * @author: RojiaHuang
 * @description:
 * @date: 2021/4/12
 */
@Configuration
public class ThreadPoolConfig {
    @Value("${asyncPool-corePoolSize}")
    private int asyncCorePoolSize;
    @Value("${asyncPool-maxPoolSize}")
    private int asyncMaxPoolSize;
    @Value("${asyncPool-queueCapacity}")
    private int asyncQueueCapacity;

    @Bean(name = "asyncPool")
    public Executor checkThreadPool() {
        ThreadPoolTaskExecutor taskExecutor = new ThreadPoolTaskExecutor();
        taskExecutor.setCorePoolSize(asyncCorePoolSize);
        taskExecutor.setMaxPoolSize(asyncMaxPoolSize);
        taskExecutor.setQueueCapacity(asyncQueueCapacity);
        taskExecutor.setKeepAliveSeconds(240);
        taskExecutor.setThreadNamePrefix("async-thread-");
        taskExecutor.setRejectedExecutionHandler(new ThreadPoolExecutor.CallerRunsPolicy());
        taskExecutor.setWaitForTasksToCompleteOnShutdown(true);
        taskExecutor.setAwaitTerminationSeconds(60);
        taskExecutor.initialize();
        return taskExecutor;
    }
}
