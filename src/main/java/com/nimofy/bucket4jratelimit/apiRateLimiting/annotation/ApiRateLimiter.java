package com.nimofy.bucket4jratelimit.apiRateLimiting.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface ApiRateLimiter {

    RateLimitTimeUnit timeUnit() default RateLimitTimeUnit.MINUTES;

    long numberOfAllowedCalls();

    long timePeriod() default 1;

    String argumentName();
}