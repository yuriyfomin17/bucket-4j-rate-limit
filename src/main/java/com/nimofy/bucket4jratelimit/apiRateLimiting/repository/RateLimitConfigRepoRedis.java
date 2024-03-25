package com.nimofy.bucket4jratelimit.apiRateLimiting.repository;

import com.nimofy.bucket4jratelimit.apiRateLimiting.modelRedis.ThirdPartyApiRateLimitConfigRedis;
import org.springframework.data.repository.CrudRepository;

public interface RateLimitConfigRepoRedis extends CrudRepository<ThirdPartyApiRateLimitConfigRedis, Integer> { }