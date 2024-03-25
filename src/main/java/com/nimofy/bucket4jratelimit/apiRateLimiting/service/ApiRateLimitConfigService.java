package com.nimofy.bucket4jratelimit.apiRateLimiting.service;

import com.nimofy.bucket4jratelimit.apiRateLimiting.annotation.ApiRateLimiter;
import com.nimofy.bucket4jratelimit.apiRateLimiting.modelRedis.ThirdPartyApiRateLimitConfigRedis;
import com.nimofy.bucket4jratelimit.apiRateLimiting.repository.RateLimitConfigRepoRedis;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RedissonClient;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.concurrent.TimeUnit;

@Service
@RequiredArgsConstructor
@Slf4j
public class ApiRateLimitConfigService {

    private static final Integer LOCK_TIMEOUT = 1;

    private final RateLimitConfigRepoRedis rateLimitConfigRepoRedis;
    private final RedissonClient redissonClient;

    public boolean isApiRateLimitExceeded(Integer argumentHashCode, ApiRateLimiter apiRateLimiterAnnotationParam) {
        try {
            boolean lockAcquired = redissonClient.getLock(argumentHashCode.toString()).tryLock(LOCK_TIMEOUT, TimeUnit.SECONDS);
            if (!lockAcquired) {
                return false;
            }
            LocalDateTime currentTime = LocalDateTime.now();
            ThirdPartyApiRateLimitConfigRedis thirdPartyApiRateLimitConfig = getOrCreateConfigByHashCode(argumentHashCode, apiRateLimiterAnnotationParam);
            if (currentTime.isAfter(thirdPartyApiRateLimitConfig.getExpirationTime())) {
                rateLimitConfigRepoRedis.deleteById(argumentHashCode);
                createApiRateLimitConfig(argumentHashCode, apiRateLimiterAnnotationParam, 1L);
                return false;
            }
            if (thirdPartyApiRateLimitConfig.getCurrentRequestCount() >= thirdPartyApiRateLimitConfig.getNumberOfAllowedCalls()) {
                return true;
            }
            thirdPartyApiRateLimitConfig.setCurrentRequestCount(thirdPartyApiRateLimitConfig.getCurrentRequestCount() + 1);
            rateLimitConfigRepoRedis.save(thirdPartyApiRateLimitConfig);
            return false;
        } catch (Exception e) {
            log.error(e.getMessage());
            return false;
        } finally {
            redissonClient.getLock(argumentHashCode.toString()).unlock();
        }
    }

    private ThirdPartyApiRateLimitConfigRedis getOrCreateConfigByHashCode(Integer argumentHash, ApiRateLimiter apiRateLimiterAnnotationParam) {
        return rateLimitConfigRepoRedis
                .findById(argumentHash)
                .orElseGet(() -> createApiRateLimitConfig(argumentHash, apiRateLimiterAnnotationParam, 0L));
    }

    private ThirdPartyApiRateLimitConfigRedis createApiRateLimitConfig(Integer argumentHash, ApiRateLimiter apiRateLimiterAnnotationParam, Long currentRequestCount) {
        return rateLimitConfigRepoRedis.save(ThirdPartyApiRateLimitConfigRedis.builder()
                .id(argumentHash)
                .currentRequestCount(currentRequestCount)
                .numberOfAllowedCalls(apiRateLimiterAnnotationParam.numberOfAllowedCalls())
                .expirationTime(getExpirationTime(apiRateLimiterAnnotationParam))
                .build()
        );
    }

    private LocalDateTime getExpirationTime(ApiRateLimiter apiRateLimiterAnnotationParam) {
        return switch (apiRateLimiterAnnotationParam.timeUnit()) {
            case HOURS -> LocalDateTime.now().plusHours(apiRateLimiterAnnotationParam.timePeriod());
            case MINUTES -> LocalDateTime.now().plusMinutes(apiRateLimiterAnnotationParam.timePeriod());
        };
    }
}