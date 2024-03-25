package com.nimofy.bucket4jratelimit.apiRateLimiting.customConfig;

import com.nimofy.bucket4jratelimit.apiRateLimiting.aspect.ApiRateLimiterAspect;
import org.springframework.context.annotation.Bean;

public class ApiRateLimitConfig {

    @Bean
    public ApiRateLimiterAspect apiRateLimiterAspect(){
        return new ApiRateLimiterAspect();
    }
}