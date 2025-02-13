package com.pawlik.przemek.onlinegradebook.service.notification;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.pawlik.przemek.onlinegradebook.dto.notification.NotificationDto;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
@Profile("!production")
public class LocalNotificationService implements NotificationService {

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public void send(List<String> emailAddresses, String message) {
        NotificationDto notificationDto = new NotificationDto(emailAddresses, message);

        try {
            String json = objectMapper.writeValueAsString(notificationDto);
            log.info("Notification JSON: {}", json);
        } catch (JsonProcessingException e) {
            log.error("Error converting notification to JSON: {}", e.getMessage());
        }
    }
}
