package com.nimofy.bucket4jratelimit.apiRateLimiting.repository;

import com.nimofy.bucket4jratelimit.apiRateLimiting.model.ThirdPartyApiRateLimitConfig;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import java.util.Optional;

public interface RateLimitConfigRepo extends CrudRepository<ThirdPartyApiRateLimitConfig, Integer> {

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("select thirdPartyConfig from ThirdPartyApiRateLimitConfig thirdPartyConfig where thirdPartyConfig.id =:hashCode")
    Optional<ThirdPartyApiRateLimitConfig> findByIdWithLock(Integer hashCode);
}