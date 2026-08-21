package com.bookflex.booking.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.concurrent.Executor;

/**
 * Async configuration with a custom thread pool for non-blocking operations.
 *
 * <p><b>Threads / Concurrency</b>: Event publishing, notification sending, and
 * statistics updates are handled asynchronously via this thread pool. This prevents
 * slow downstream operations from blocking the main booking request-response cycle.</p>
 *
 * <p><b>Why a custom pool instead of default?</b>
 * The default @Async executor is a SimpleAsyncTaskExecutor that creates unbounded threads.
 * In production, this is dangerous (OOM risk). Our custom pool has bounded core/max sizes
 * and a queue capacity, providing backpressure under load.</p>
 */
@Configuration
@EnableAsync
public class AsyncConfig {

    @Bean(name = "bookingTaskExecutor")
    public Executor bookingTaskExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(5);
        executor.setMaxPoolSize(10);
        executor.setQueueCapacity(50);
        executor.setThreadNamePrefix("booking-async-");
        executor.setWaitForTasksToCompleteOnShutdown(true);
        executor.setAwaitTerminationSeconds(30);
        executor.initialize();
        return executor;
    }
}
