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

    @Before("@within(apiRateLimiter)")
    public void before(JoinPoint joinPoint, ApiRateLimiter apiRateLimiter) {
        int argumentIndex = retrieveParamIndex(joinPoint, apiRateLimiter.argumentName());
        if (argumentIndex == -1) {
            return;
        }
        Object[] methodArgs = joinPoint.getArgs();
        Object methodArgument = methodArgs[argumentIndex];
        int methodArgumentHashCode = methodArgument.hashCode();

        boolean isApiRateLimitExceeded = apiRateLimitConfigService.incrementRequestCount(methodArgumentHashCode, apiRateLimiter);

        if (isApiRateLimitExceeded) {
            throw new ApiRateLimitingException("api limit exceed");
        }
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