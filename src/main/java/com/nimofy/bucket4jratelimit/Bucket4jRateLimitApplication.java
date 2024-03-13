package com.nimofy.bucket4jratelimit;

import com.nimofy.bucket4jratelimit.apiRateLimiting.EnableApiRateLimitConfig;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.context.event.EventListener;
import org.springframework.core.task.TaskExecutor;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;

@SpringBootApplication
@EnableApiRateLimitConfig
public class Bucket4jRateLimitApplication {

    public static void main(String[] args) {
        SpringApplication.run(Bucket4jRateLimitApplication.class, args);
    }

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

    @EventListener(ContextRefreshedEvent.class)
    public void test() {
        var restTemplate = new RestTemplate();
        List<CompletableFuture<Void>> completableFutures = new ArrayList<>();
        for (int i = 0; i < 10; i++) {
            completableFutures.add(CompletableFuture.runAsync(() -> {
                ResponseEntity<Integer> forEntity = restTemplate.getForEntity("http://localhost:8080/hello", Integer.class);
                System.out.println("count:" + forEntity.getBody());
            }, cryptoTaskExecutor()));
        }
        completableFutures.forEach(CompletableFuture::join);
    }
}