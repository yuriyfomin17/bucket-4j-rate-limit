package com.nimofy.bucket4jratelimit.apiRateLimiting;


import lombok.RequiredArgsConstructor;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;

@Aspect
@RequiredArgsConstructor
public class ApiRateLimiterAspect {
    private final ApiRateLimiterAnnotationBeanPostProcessor apiRateLimiterAnnotationBeanPostProcessor;
}