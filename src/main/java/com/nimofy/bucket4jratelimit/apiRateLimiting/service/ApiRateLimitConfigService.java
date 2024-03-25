package com.nimofy.bucket4jratelimit.apiRateLimiting.service;

import com.nimofy.bucket4jratelimit.apiRateLimiting.annotation.ApiRateLimiter;
import com.nimofy.bucket4jratelimit.apiRateLimiting.model.ThirdPartyApiRateLimitConfig;
import com.nimofy.bucket4jratelimit.apiRateLimiting.repository.RateLimitConfigRepo;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
@Slf4j
public class ApiRateLimitConfigService {

    private final RateLimitConfigRepo rateLimitConfigRepo;

    @Transactional
    public boolean incrementRequestCount(Integer argumentHashCode, ApiRateLimiter apiRateLimiterAnnotationParam) {
        try {
            LocalDateTime currentTime = LocalDateTime.now();
            ThirdPartyApiRateLimitConfig thirdPartyApiRateLimitConfig = retrieveConfigByHashcode(argumentHashCode, apiRateLimiterAnnotationParam);
            if (currentTime.isAfter(thirdPartyApiRateLimitConfig.getExpirationTime())) {
                thirdPartyApiRateLimitConfig.setCurrentRequestCount(1L);
                thirdPartyApiRateLimitConfig.setExpirationTime(getExpirationTime(apiRateLimiterAnnotationParam));
                return false;
            }

            if (thirdPartyApiRateLimitConfig.getCurrentRequestCount() >= thirdPartyApiRateLimitConfig.getNumberOfAllowedCalls()) {
                return true;
            }

            thirdPartyApiRateLimitConfig.setCurrentRequestCount(thirdPartyApiRateLimitConfig.getCurrentRequestCount() + 1);
            return false;
        } catch (Exception e) {
            log.error(e.getMessage());
            return false;
        }
    }


    private ThirdPartyApiRateLimitConfig retrieveConfigByHashcode(Integer argumentHash, ApiRateLimiter apiRateLimiterAnnotationParam) {
        return rateLimitConfigRepo
                .findByIdWithLock(argumentHash)
                .orElseGet(() -> rateLimitConfigRepo.save(ThirdPartyApiRateLimitConfig.builder()
                        .id(argumentHash)
                        .currentRequestCount(0L)
                        .numberOfAllowedCalls(apiRateLimiterAnnotationParam.numberOfAllowedCalls())
                        .expirationTime(getExpirationTime(apiRateLimiterAnnotationParam))
                        .build()));
    }

    private LocalDateTime getExpirationTime(ApiRateLimiter apiRateLimiterAnnotationParam) {
        return switch (apiRateLimiterAnnotationParam.timeUnit()) {
            case HOURS -> LocalDateTime.now().plusHours(apiRateLimiterAnnotationParam.timePeriod());
            case MINUTES -> LocalDateTime.now().plusMinutes(apiRateLimiterAnnotationParam.timePeriod());
        };
    }
}