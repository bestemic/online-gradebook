package com.bestemic.notification_server;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@Configuration
@EnableScheduling
@SpringBootApplication
@RestController
public class NotificationServerApplication {

    public static void main(String[] args) {
        SpringApplication.run(NotificationServerApplication.class, args);
    }

    @GetMapping("/")
    public String healthCheck() {
        return "OK";
    }
}
