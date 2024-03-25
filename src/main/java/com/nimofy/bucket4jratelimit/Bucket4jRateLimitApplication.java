package com.nimofy.bucket4jratelimit;

import com.nimofy.bucket4jratelimit.apiRateLimiting.annotation.EnableApiRateLimitConfig;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.context.event.EventListener;
import org.springframework.core.task.TaskExecutor;
import org.springframework.http.HttpStatus;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;

@SpringBootApplication
@EnableApiRateLimitConfig
public class Bucket4jRateLimitApplication {

    @Autowired
    private RestTemplate restTemplate;

    @Autowired
    private TaskExecutor cryptoTaskExecutor;

    public static void main(String[] args) {
        SpringApplication.run(Bucket4jRateLimitApplication.class, args);
    }

    @EventListener(ContextRefreshedEvent.class)
    public void testRateLimiting() {
        List<CompletableFuture<HttpStatus>> completableFutures = new ArrayList<>();
        restTemplate.getForEntity("http://localhost:8080/hello", HttpStatus.class).getBody();
        for (int i = 0; i <= 10_000; i++) {
            completableFutures.add(CompletableFuture.supplyAsync(() -> {
                        try {
                            return restTemplate.getForEntity("http://localhost:8080/hello", HttpStatus.class).getBody();
                        } catch (Exception e) {
                            return HttpStatus.BAD_GATEWAY;
                        }
                    }, cryptoTaskExecutor)
            );
        }
        completableFutures.forEach(CompletableFuture::join);
    }
}