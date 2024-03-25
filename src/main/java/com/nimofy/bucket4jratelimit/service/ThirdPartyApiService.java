package com.nimofy.bucket4jratelimit.service;

import com.nimofy.bucket4jratelimit.apiRateLimiting.annotation.ApiRateLimiter;
import com.nimofy.bucket4jratelimit.apiRateLimiting.annotation.RateLimitTimeUnit;
import org.springframework.stereotype.Service;

import java.util.concurrent.atomic.AtomicInteger;

@ApiRateLimiter(timeUnit = RateLimitTimeUnit.MINUTES, numberOfAllowedCalls = 10_000, argumentName = "arg4")
@Service
public class ThirdPartyApiService {

    private static final AtomicInteger atomicInteger = new AtomicInteger(1);

    public void testMethod(String arg1, String arg2, String arg4) {
        System.out.println("current count:" + atomicInteger.getAndIncrement());
    }
}