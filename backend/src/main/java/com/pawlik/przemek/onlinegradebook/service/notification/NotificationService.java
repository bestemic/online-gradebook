package com.pawlik.przemek.onlinegradebook.service.notification;

import java.util.List;

public interface NotificationService {
    void send(List<String> emailAddresses, String message);
}