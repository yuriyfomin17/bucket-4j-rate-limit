package com.nimofy.bucket4jratelimit.apiRateLimiting;


import io.github.bucket4j.Bandwidth;
import io.github.bucket4j.Bucket;
import lombok.RequiredArgsConstructor;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;

import java.time.Duration;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.BiFunction;

@Aspect
@RequiredArgsConstructor
public class ApiRateLimiterAspect {
    private static final BiFunction<Long, RateLimitTimeUnit, Bandwidth> bandWidthCreator = (value, timeUnit) -> switch (timeUnit) {
        case HOURS -> Bandwidth.simple(value, Duration.ofHours(1));
        case MINUTES -> Bandwidth.simple(value, Duration.ofMinutes(1));
    };
    private static final String RATE_LIMITER_SUFFIX = "RATE_LIMITER_SUFFIX";
    private final Map<String, Bucket> rateLimiters = new ConcurrentHashMap<>();

    @Before("@within(apiRateLimiter)")
    public void before(JoinPoint joinPoint, ApiRateLimiter apiRateLimiter) {
        String beanName = joinPoint.getSignature().getDeclaringType().getSimpleName();
        Bucket currentApiBeanLimiter = rateLimiters.computeIfAbsent(beanName + RATE_LIMITER_SUFFIX, key -> createRateLimitingBucket(apiRateLimiter.timeValue(), apiRateLimiter.timeUnit()));

        if (currentApiBeanLimiter == null) {
            throw new ApiRateLimitingException(String.format("Api rate limiter [%s] does not exist", beanName + RATE_LIMITER_SUFFIX));
        }
        boolean apiCallConsumed = currentApiBeanLimiter.tryConsume(1);
        if (!apiCallConsumed) {
            throw new ApiRateLimitingException("Api call limit exceeded");
        }
    }

    public Bucket createRateLimitingBucket(long timeValue, RateLimitTimeUnit timeUnit) {
        return Bucket.builder()
                .addLimit(bandWidthCreator.apply(timeValue, timeUnit))
                .build();
    }
}