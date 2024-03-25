package com.nimofy.bucket4jratelimit.apiRateLimiting.modelRedis;


import lombok.*;
import org.springframework.data.annotation.Id;

import org.springframework.data.redis.core.RedisHash;

import java.time.LocalDateTime;

@Getter
@Setter
@EqualsAndHashCode(of = {"id"})
@ToString
@RedisHash("ThirdPartyApiRateLimitConfigRedis")
@Builder
public class ThirdPartyApiRateLimitConfigRedis {
    @Id
    private Integer id;

    private Long currentRequestCount;

    private Long numberOfAllowedCalls;

    private LocalDateTime expirationTime;
}