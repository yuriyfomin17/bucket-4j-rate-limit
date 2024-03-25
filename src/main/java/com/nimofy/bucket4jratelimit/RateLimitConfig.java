package com.nimofy.bucket4jratelimit;

import org.redisson.Redisson;
import org.redisson.api.RedissonClient;
import org.redisson.config.Config;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.task.TaskExecutor;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.web.client.RestTemplate;

@Configuration
public class RateLimitConfig {

    @Bean
    public RestTemplate restTemplate() {
        return new RestTemplate();
    }

    @Bean
    public TaskExecutor cryptoTaskExecutor() {
        ThreadPoolTaskExecutor taskExecutor = new ThreadPoolTaskExecutor();
        taskExecutor.setCorePoolSize(100);
        taskExecutor.setMaxPoolSize(100);
        return taskExecutor;
    }

    @Bean
    public Config redissonConfig() {
        Config config = new Config();
        // Set connection pool size
        config.useSingleServer()
                .setAddress("redis://localhost:6379")
                .setConnectionPoolSize(100);
        return config;
    }

    @Bean
    public RedissonClient redissonClient(Config commonConf) {
        return Redisson.create(commonConf);
    }
}