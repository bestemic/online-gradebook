package com.pawlik.przemek.onlinegradebook.service.notification;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.pawlik.przemek.onlinegradebook.dto.notification.NotificationDto;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Profile("!production")
public class LocalNotificationService implements NotificationService {

    private final static Logger LOGGER = LoggerFactory.getLogger(LocalNotificationService.class);
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public void send(List<String> emailAddresses, String message) {
        NotificationDto notificationDto = new NotificationDto(emailAddresses, message);

        try {
            String json = objectMapper.writeValueAsString(notificationDto);
            LOGGER.info("Notification JSON: " + json);
        } catch (JsonProcessingException e) {
            LOGGER.error("Error converting notification to JSON: " + e.getMessage());
        }
    }
}