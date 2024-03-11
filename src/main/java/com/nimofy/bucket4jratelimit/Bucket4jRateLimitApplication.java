package com.nimofy.bucket4jratelimit;

import com.nimofy.bucket4jratelimit.apiRateLimiting.EnableApiRateLimitConfig;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.web.client.RestTemplate;

@SpringBootApplication
@EnableApiRateLimitConfig
public class Bucket4jRateLimitApplication {

    public static void main(String[] args) {
        SpringApplication.run(Bucket4jRateLimitApplication.class, args);
    }

    @Bean
    public RestTemplate restTemplate(){
        return new RestTemplate();
    }
}