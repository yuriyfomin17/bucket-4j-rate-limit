package com.nimofy.bucket4jratelimit.apiRateLimiting;

import org.springframework.context.annotation.Bean;

public class ApiRateLimitConfig {

    @Bean
    public ApiRateLimiterAspect apiRateLimiterAspect(){
        return new ApiRateLimiterAspect();
    }
}