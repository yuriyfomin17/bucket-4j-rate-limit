package com.nimofy.bucket4jratelimit.apiRateLimiting.aspect;

import com.nimofy.bucket4jratelimit.apiRateLimiting.exception.ApiRateLimitingException;
import com.nimofy.bucket4jratelimit.apiRateLimiting.annotation.ApiRateLimiter;
import com.nimofy.bucket4jratelimit.apiRateLimiting.service.ApiRateLimitConfigService;
import org.apache.commons.lang3.StringUtils;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.beans.factory.annotation.Autowired;

import java.lang.reflect.Method;
import java.lang.reflect.Parameter;


@Aspect
public class ApiRateLimiterAspect {

    @Autowired
    private ApiRateLimitConfigService apiRateLimitConfigService;

    private static final String CLASS_ID = "-CLASS_ID";

    @Before("@within(apiRateLimiter)")
    public void before(JoinPoint joinPoint, ApiRateLimiter apiRateLimiter) {
        Integer methodArgumentHashCode = getMethodArgumentHashCode(joinPoint, apiRateLimiter);
        if (methodArgumentHashCode == null) return;
        boolean isApiRateLimitExceeded = apiRateLimitConfigService.isApiRateLimitExceeded(methodArgumentHashCode, apiRateLimiter);
        if (isApiRateLimitExceeded) {
            throw new ApiRateLimitingException("api limit exceed");
        }
    }

    private Integer getMethodArgumentHashCode(JoinPoint joinPoint, ApiRateLimiter apiRateLimiter) {
        if (StringUtils.isEmpty(apiRateLimiter.rateLimitedArgument())){
            return (joinPoint.getTarget().getClass().getName() + CLASS_ID)
                    .hashCode();
        }
        int argumentIndex = retrieveParamIndex(joinPoint, apiRateLimiter.rateLimitedArgument());
        if (argumentIndex == -1) {
            return null;
        }
        Object[] methodArgs = joinPoint.getArgs();
        Object methodArgument = methodArgs[argumentIndex];
        return methodArgument.hashCode();
    }


    private int retrieveParamIndex(JoinPoint joinPoint, String paramName) {
        Method method = ((MethodSignature) joinPoint.getSignature()).getMethod();
        Parameter[] parameters = method.getParameters();
        for (int i = 0; i < parameters.length; i++) {
            Parameter parameter = parameters[i];
            if (StringUtils.equalsIgnoreCase(parameter.getName(), paramName)) {
                return i;
            }
        }
        return -1;
    }
}