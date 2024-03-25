package com.nimofy.bucket4jratelimit.rest;

import com.nimofy.bucket4jratelimit.service.ThirdPartyApiService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class MyRestController {

    private final ThirdPartyApiService thirdPartyApiService;

    @GetMapping("hello")
    public ResponseEntity<HttpStatus> hello() {
        System.out.println("hello from thread:" + Thread.currentThread().getName());
        thirdPartyApiService.testMethod("arg1", "arg2", "arg3");
        return ResponseEntity.ok().build();
    }
}