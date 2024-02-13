package com.bestemic.onlinegradebook.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class TestController {

    @GetMapping("/test")
    public String testMethod() {
        return "Test response for Security configuration.";
    }
}
