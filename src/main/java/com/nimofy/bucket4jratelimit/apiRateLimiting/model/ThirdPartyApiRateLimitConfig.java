package com.nimofy.bucket4jratelimit.apiRateLimiting.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.*;

import java.time.LocalDateTime;

@ToString
@Entity
@Table(name = "third_party_api_rate_limit_config")
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Data
public class ThirdPartyApiRateLimitConfig {

    @Id
    private Integer id;

    @Column(name = "current_request_count", nullable = false)
    private Long currentRequestCount;

    @Column(name = "request_threshold", nullable = false)
    private Long numberOfAllowedCalls;

    @Column(name = "expiration_time", nullable = false)
    private LocalDateTime expirationTime;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof ThirdPartyApiRateLimitConfig thirdPartyApiRateLimitConfig)) return false;
        return id != null && id.equals(thirdPartyApiRateLimitConfig.getId());
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }
}
