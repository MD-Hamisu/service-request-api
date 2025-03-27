package com.genysyxtechnologies.service_request_system.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

@Configuration
@EnableAsync
public class AsyncConfig {

    @Bean
    public ThreadPoolTaskExecutor asyncTaskExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        // Using virtual threads for better scalability
        executor.setTaskDecorator(runnable -> Thread.ofVirtual().unstarted(runnable));
        executor.setCorePoolSize(10); // Number of threads to keep in the pool
        executor.setMaxPoolSize(50); // Maximum number of threads
        executor.setQueueCapacity(500); // Queue capacity for tasks
        executor.setThreadNamePrefix("async-");
        executor.initialize();
        return executor;
    }
}
