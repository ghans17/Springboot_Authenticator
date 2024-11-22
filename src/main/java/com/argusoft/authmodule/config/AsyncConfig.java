package com.argusoft.authmodule.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.concurrent.Executor;


@Configuration
@EnableAsync
public class AsyncConfig {

    @Bean
    public Executor taskExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(5);  // Minimum number of threads in the pool
        executor.setMaxPoolSize(10);  // Maximum number of threads
        executor.setQueueCapacity(50); // Tasks that can wait in the queue
        executor.setThreadNamePrefix("EmailExecutor-");
        executor.initialize(); //Initialize the thread pool
        return executor;
    }
}