package com.pawlik.przemek.onlinegradebook.service.notification;

import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Profile("production")
public class CloudNotificationService implements NotificationService {

    @Override
    public void send(List<String> emailAddresses, String message) {

    }
}
