package com.nimofy.bucket4jratelimit.apiRateLimiting.annotation;

import com.nimofy.bucket4jratelimit.apiRateLimiting.customConfig.ApiRateLimitConfig;
import org.springframework.context.annotation.Import;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Import(ApiRateLimitConfig.class)
public @interface EnableApiRateLimitConfig {
}
