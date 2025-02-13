package com.pawlik.przemek.onlinegradebook.service.notification;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.cloud.pubsub.v1.Publisher;
import com.google.protobuf.ByteString;
import com.google.pubsub.v1.PubsubMessage;
import com.google.pubsub.v1.TopicName;
import com.pawlik.przemek.onlinegradebook.dto.notification.NotificationDto;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.TimeUnit;

@Slf4j
@Service
@Profile("production")
public class CloudNotificationService implements NotificationService {

    private final ObjectMapper objectMapper = new ObjectMapper();
    private final String topicId;
    private final String projectId;

    public CloudNotificationService(@Value("${gcp.pubsub.project-id}") String projectId, @Value("${gcp.pubsub.topic-id}") String topicId) {
        this.projectId = projectId;
        this.topicId = topicId;
    }

    @Override
    public void send(List<String> emailAddresses, String message) {
        NotificationDto notificationDto = new NotificationDto(emailAddresses, message);

        try {
            String json = objectMapper.writeValueAsString(notificationDto);
            publishMessage(json);
        } catch (JsonProcessingException e) {
            log.error("Error converting notification to JSON: {}", e.getMessage());
        } catch (Exception e) {
            log.error("Error sending notification to PubSub: {}", e.getMessage());
        }
    }

    private void publishMessage(String message) throws IOException, InterruptedException {
        TopicName topicName = TopicName.of(projectId, topicId);
        Publisher publisher = null;

        try {
            publisher = Publisher.newBuilder(topicName).build();
            ByteString data = ByteString.copyFromUtf8(message);
            PubsubMessage pubsubMessage = PubsubMessage.newBuilder()
                    .setData(data)
                    .build();
            publisher.publish(pubsubMessage);
        } finally {
            if (publisher != null) {
                publisher.shutdown();
                publisher.awaitTermination(1, TimeUnit.MINUTES);
            }
        }
    }
}
