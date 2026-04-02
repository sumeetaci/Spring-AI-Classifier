package com.restai.config;

import java.util.concurrent.Executor;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

@Configuration
@EnableAsync // Activates Spring's ability to run @Async methods
public class AsyncConfig {
	@Bean(name = "pythonTaskExecutor") // Give your executor a unique name
    public Executor customTaskExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(2);
        executor.setMaxPoolSize(5);  // 5 tasks can run in parallel
        executor.setQueueCapacity(500); // Queue capacity
        executor.setThreadNamePrefix("pythonAsync-"); // Thread name prefix
        executor.initialize();
        return executor;
    }
}
