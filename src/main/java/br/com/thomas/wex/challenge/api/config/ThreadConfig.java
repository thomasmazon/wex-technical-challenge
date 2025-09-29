package br.com.thomas.wex.challenge.api.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.task.TaskExecutor;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

/**
 * @author Thomas J. Mazon de Oiveira
 */
@Configuration
@EnableAsync
public class ThreadConfig {
 
    @Bean(name = "threadPoolExecutor")
    public TaskExecutor threadPoolMissionsExecutor() {
 
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(5);
        executor.setMaxPoolSize(10);
        executor.setQueueCapacity(500);
        executor.setThreadNamePrefix("wex-thread-executor-async-");
        executor.initialize();
 
        return executor;
    }
}