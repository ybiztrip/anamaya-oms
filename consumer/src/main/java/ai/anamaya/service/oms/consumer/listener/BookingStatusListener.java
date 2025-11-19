package ai.anamaya.service.oms.consumer.listener;

import ai.anamaya.service.oms.core.dto.pubsub.BookingStatusMessage;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.cloud.spring.pubsub.core.subscriber.PubSubSubscriberTemplate;
import com.google.cloud.spring.pubsub.support.BasicAcknowledgeablePubsubMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;

@Slf4j
@Service
@RequiredArgsConstructor
public class BookingStatusListener {

    private final PubSubSubscriberTemplate subscriberTemplate;
    private final ObjectMapper objectMapper = new ObjectMapper();

    private static final String SUBSCRIPTION = "oms-booking-status-sub";

    @PostConstruct
    public void subscribe() {
        subscriberTemplate.subscribe(SUBSCRIPTION, this::handleMessage);
        log.info("Subscribed to Pub/Sub: {}", SUBSCRIPTION);
    }

    private void handleMessage(BasicAcknowledgeablePubsubMessage message) {
        try {
            String data = message.getPubsubMessage().getData().toStringUtf8();
            log.info("Received PubSub message: {}", data);

            BookingStatusMessage bookingStatus =
                objectMapper.readValue(data, BookingStatusMessage.class);

            log.info("Booking ID: {}, Status: {}", bookingStatus.getBookingId(), bookingStatus.getStatus());

            message.ack();
        } catch (Exception e) {
            log.error("Error processing message", e);
            message.nack();
        }
    }
}
