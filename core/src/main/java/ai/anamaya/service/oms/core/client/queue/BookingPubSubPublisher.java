package ai.anamaya.service.oms.core.client.queue;

import ai.anamaya.service.oms.core.dto.pubsub.BookingStatusMessage;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.cloud.spring.pubsub.core.PubSubTemplate;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class BookingPubSubPublisher {
    private final PubSubTemplate pubSubTemplate;
    private final ObjectMapper mapper;

    private final String topicId = "oms-booking-status";

    public void publishBookingStatus(BookingStatusMessage message) {
        try {
            String json = mapper.writeValueAsString(message);
            pubSubTemplate.publish(topicId, json);
            log.info("Published booking status message: {}", json);
        } catch (Exception e) {
            log.error("Failed to publish booking status", e);
        }
    }
}

