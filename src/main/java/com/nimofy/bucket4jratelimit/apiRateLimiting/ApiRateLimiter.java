package com.nimofy.bucket4jratelimit.apiRateLimiting;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface ApiRateLimiter {

    RateLimitTimeUnit timeUnit() default RateLimitTimeUnit.MINUTES;

    long timeValue();
}