package com.nimofy.bucket4jratelimit;

import com.nimofy.bucket4jratelimit.apiRateLimiting.ApiRateLimiter;
import com.nimofy.bucket4jratelimit.apiRateLimiting.RateLimitTimeUnit;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.concurrent.atomic.AtomicInteger;

@RestController
@RequiredArgsConstructor
@ApiRateLimiter(timeUnit = RateLimitTimeUnit.HOURS, timeValue = 5)
public class MyRestController {

    private static final AtomicInteger atomicInteger = new AtomicInteger(0);

    @GetMapping("hello")
    public Integer hello() {
        return atomicInteger.incrementAndGet();
    }
}