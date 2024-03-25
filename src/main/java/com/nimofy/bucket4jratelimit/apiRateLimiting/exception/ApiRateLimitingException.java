package com.nimofy.bucket4jratelimit.apiRateLimiting.exception;

public class ApiRateLimitingException extends RuntimeException{

    public ApiRateLimitingException(String errorMessage){
        super(String.format(errorMessage));
    }
}
