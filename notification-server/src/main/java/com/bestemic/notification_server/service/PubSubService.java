package com.bestemic.notification_server.service;

import com.bestemic.notification_server.dto.NotificationDto;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.cloud.pubsub.v1.AckReplyConsumer;
import com.google.cloud.pubsub.v1.MessageReceiver;
import com.google.cloud.pubsub.v1.Subscriber;
import com.google.protobuf.ByteString;
import com.google.pubsub.v1.ProjectSubscriptionName;
import com.google.pubsub.v1.PubsubMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

@Service
public class PubSubService {

    @Value("${gcp.pubsub.project-id}")
    private String projectId;

    @Value("${gcp.pubsub.subscription-id}")
    private String subscriptionId;

    private final static Logger LOGGER = LoggerFactory.getLogger(PubSubService.class);
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Scheduled(fixedRate = 300000)
    public void pollMessages() {
        ProjectSubscriptionName subscriptionName = ProjectSubscriptionName.of(projectId, subscriptionId);

        MessageReceiver receiver =
                (PubsubMessage message, AckReplyConsumer consumer) -> {
                    ByteString data = message.getData();
                    try {
                        NotificationDto notificationDto = objectMapper.readValue(data.toStringUtf8(), NotificationDto.class);
                        LOGGER.info("Received message: [{}] to [{}]", notificationDto.getMessage(), notificationDto.getEmailAddresses().toString());
                        consumer.ack();
                    } catch (JsonProcessingException e) {
                        LOGGER.error("Error processing message: {} with exception", data.toStringUtf8(), e);
                    }
                };

        Subscriber subscriber = null;
        try {
            subscriber = Subscriber.newBuilder(subscriptionName, receiver).build();
            subscriber.startAsync().awaitRunning();
            subscriber.awaitTerminated(30, TimeUnit.SECONDS);
        } catch (TimeoutException timeoutException) {
            subscriber.stopAsync();
        }
    }
}
