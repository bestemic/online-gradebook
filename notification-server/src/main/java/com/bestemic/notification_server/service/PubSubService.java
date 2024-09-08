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

    private final static Logger LOGGER = LoggerFactory.getLogger(PubSubService.class);
    private final ObjectMapper objectMapper = new ObjectMapper();
    private final ProjectSubscriptionName subscriptionName;
    private String subscriptionId;


    public PubSubService(@Value("${gcp.pubsub.project-id}") String projectId, @Value("${gcp.pubsub.subscription-id}") String subscriptionId) {
        this.subscriptionId = subscriptionId;
        this.subscriptionName = ProjectSubscriptionName.of(projectId, subscriptionId);
    }

    @Scheduled(initialDelay = 60, fixedDelay = 4 * 60, timeUnit = TimeUnit.SECONDS)
    public void pollMessages() {
        LOGGER.info("Polling messages from PubSub subscription: [{}]", subscriptionId);

        MessageReceiver receiver =
                (PubsubMessage message, AckReplyConsumer consumer) -> {
                    ByteString data = message.getData();
                    try {
                        NotificationDto notificationDto = objectMapper.readValue(data.toStringUtf8(), NotificationDto.class);
                        LOGGER.info("Received message: [{}] to [{}]", notificationDto.getMessage(), notificationDto.getEmailAddresses().toString());
                    } catch (JsonProcessingException e) {
                        LOGGER.error("Error processing message: {} with exception", data.toStringUtf8(), e);
                    }
                    consumer.ack();
                    LOGGER.info("Acknowledged message");
                };

        Subscriber subscriber = null;
        LOGGER.info("Starting subscriber");
        try {
            subscriber = Subscriber.newBuilder(subscriptionName, receiver).build();
            LOGGER.info("Subscriber started");
            subscriber.startAsync().awaitRunning();
            LOGGER.info("Subscriber running");
            subscriber.awaitTerminated(30, TimeUnit.SECONDS);
        } catch (TimeoutException timeoutException) {
            subscriber.stopAsync();
        }
        LOGGER.info("Finished polling messages from PubSub subscription: [{}]", subscriptionId);
    }
}
