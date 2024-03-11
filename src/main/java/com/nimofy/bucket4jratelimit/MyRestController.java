package com.nimofy.bucket4jratelimit;

import com.nimofy.bucket4jratelimit.apiRateLimiting.ApiRateLimiter;
import com.nimofy.bucket4jratelimit.apiRateLimiting.RateLimitTimeUnit;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

@RestController
@RequiredArgsConstructor
@ApiRateLimiter(timeUnit = RateLimitTimeUnit.MINUTES, timeValue = 1)
public class MyRestController {

    private final RestTemplate restTemplate;
    @GetMapping("hello")
    public String hello() {
        return "hello";
    }
}