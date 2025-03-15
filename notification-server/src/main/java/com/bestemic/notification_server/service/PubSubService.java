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
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

@Service
@Slf4j
public class PubSubService {

    private final ObjectMapper objectMapper = new ObjectMapper();
    private final ProjectSubscriptionName subscriptionName;
    private Subscriber subscriber;

    public PubSubService(
            @Value("${gcp.pubsub.project-id}") String projectId,
            @Value("${gcp.pubsub.subscription-id}") String subscriptionId) {
        this.subscriptionName = ProjectSubscriptionName.of(projectId, subscriptionId);
    }

    @PostConstruct
    public void start() {
        log.info("Starting Pub/Sub subscriber...");
        MessageReceiver receiver =
                (PubsubMessage message, AckReplyConsumer consumer) -> {
                    ByteString data = message.getData();
                    try {
                        NotificationDto notificationDto = objectMapper.readValue(data.toStringUtf8(), NotificationDto.class);
                        log.info("Received message: [{}] to [{}]", notificationDto.getMessage(), notificationDto.getEmailAddresses().toString());
                    } catch (JsonProcessingException e) {
                        log.error("Error processing message: {} with exception", data.toStringUtf8(), e);
                    }
                    consumer.ack();
                };

        subscriber = Subscriber.newBuilder(subscriptionName, receiver).build();
        subscriber.startAsync().awaitRunning();
        log.info("Subscriber running and awaiting messages...");
    }

    @PreDestroy
    public void stop() {
        try {
            if (subscriber != null) {
                subscriber.stopAsync().awaitTerminated(30, TimeUnit.SECONDS);
                log.info("Subscriber stopped");
            }
        } catch (TimeoutException e) {
            log.info("Error stopping subscriber", e);
        }
    }
}
