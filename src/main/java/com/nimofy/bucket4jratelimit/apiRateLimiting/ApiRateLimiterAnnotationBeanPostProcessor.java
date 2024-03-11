package com.nimofy.bucket4jratelimit.apiRateLimiting;

import io.github.bucket4j.Bandwidth;
import io.github.bucket4j.Bucket;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.cglib.proxy.Enhancer;
import org.springframework.cglib.proxy.MethodInterceptor;

import java.time.Duration;
import java.util.HashMap;
import java.util.Map;
import java.util.function.BiFunction;

public class ApiRateLimiterAnnotationBeanPostProcessor implements BeanPostProcessor {

    private static final BiFunction<Long, RateLimitTimeUnit, Bandwidth> bandWidthCreator = (value, timeUnit) -> switch (timeUnit) {
        case HOURS -> Bandwidth.simple(value, Duration.ofHours(1));
        case MINUTES -> Bandwidth.simple(value, Duration.ofMinutes(1));
    };

    private static final String RATE_LIMITER_SUFFIX = "RATE_LIMITER_SUFFIX";
    private final Map<String, Class<?>> beanToBeProxied = new HashMap<>();
    private final Map<String, Bucket> rateLimiters = new HashMap<>();

    @Override
    public Object postProcessBeforeInitialization(Object bean, String beanName) throws BeansException {

        if (bean.getClass().isAnnotationPresent(ApiRateLimiter.class)) {
            beanToBeProxied.put(beanName, bean.getClass());
            rateLimiters.put(beanName + RATE_LIMITER_SUFFIX, createRateLimitingBucket(getTimeValue(bean), getRateLimitTimeUnit(bean)));
        }
        return BeanPostProcessor.super.postProcessBeforeInitialization(bean, beanName);
    }

    @Override
    public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {
        Class<?> beanToProxy = beanToBeProxied.get(beanName);
        if (beanToProxy != null) {
            Enhancer enhancer = new Enhancer();
            enhancer.setSuperclass(beanToProxy);
            enhancer.setCallback(rateLimitingInterceptor(beanName));
            return enhancer.create();
        }

        return BeanPostProcessor.super.postProcessAfterInitialization(bean, beanName);
    }

    private MethodInterceptor rateLimitingInterceptor(String beanName) {
        return (beanInstance, method, args, methodProxy) ->
                methodProxy.invokeSuper(beanInstance, processParameters(beanName, args));
    }

    private Object[] processParameters(String beanName, Object[] args) {
        Bucket apiRateLimiter = rateLimiters.get(beanName + RATE_LIMITER_SUFFIX);
        if (apiRateLimiter == null) {
            throw new ApiRateLimitingException(String.format("Api rate limiter [%s] does not exist", beanName + RATE_LIMITER_SUFFIX));
        }
        boolean apiCallConsumed = apiRateLimiter.tryConsume(1);
        if (!apiCallConsumed) {
            throw new ApiRateLimitingException("Api call limit exceeded");
        }
        return args;
    }

    private long getTimeValue(Object bean) {
        return bean.getClass()
                .getAnnotation(ApiRateLimiter.class)
                .timeValue();
    }

    private RateLimitTimeUnit getRateLimitTimeUnit(Object bean) {
        return bean.getClass()
                .getAnnotation(ApiRateLimiter.class)
                .timeUnit();
    }

    public Bucket createRateLimitingBucket(long timeValue, RateLimitTimeUnit timeUnit) {
        return Bucket.builder()
                .addLimit(bandWidthCreator.apply(timeValue, timeUnit))
                .build();
    }
}