package com.pawlik.przemek.onlinegradebook.service.notification;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.cloud.pubsub.v1.Publisher;
import com.google.protobuf.ByteString;
import com.google.pubsub.v1.PubsubMessage;
import com.pawlik.przemek.onlinegradebook.dto.notification.NotificationDto;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.List;

@Service
@Profile("production")
public class CloudNotificationService implements NotificationService {

    private final static Logger LOGGER = LoggerFactory.getLogger(LocalNotificationService.class);
    private final ObjectMapper objectMapper = new ObjectMapper();
    private final String topicId;

    public CloudNotificationService(@Value("${gcp.topic.id}") String topicId) {
        this.topicId = topicId;
    }

    @Override
    public void send(List<String> emailAddresses, String message) {
        NotificationDto notificationDto = new NotificationDto(emailAddresses, message);

        try {
            String json = objectMapper.writeValueAsString(notificationDto);
            publishMessage(json);
        } catch (JsonProcessingException e) {
            LOGGER.error("Error converting notification to JSON: " + e.getMessage());
        } catch (Exception e) {
            LOGGER.error("Error sending notification to PubSub: " + e.getMessage());
        }
    }

    private void publishMessage(String message) throws IOException {
        Publisher publisher = null;

        try {
            publisher = Publisher.newBuilder(topicId).build();
            ByteString data = ByteString.copyFromUtf8(message);
            PubsubMessage pubsubMessage = PubsubMessage.newBuilder()
                    .setData(data)
                    .build();
            publisher.publish(pubsubMessage);
        } finally {
            if (publisher != null) {
                publisher.shutdown();
            }
        }
    }
}
