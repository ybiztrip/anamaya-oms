package ai.anamaya.service.oms.core.client.queue;

import ai.anamaya.service.oms.core.dto.pubsub.BookingStatusMessage;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.protobuf.ByteString;
import com.google.pubsub.v1.PubsubMessage;
import org.springframework.stereotype.Component;
import com.google.cloud.spring.pubsub.core.PubSubTemplate;

@Component
public class BookingPubSubPublisher {

    private final PubSubTemplate pubSubTemplate;

    public BookingPubSubPublisher(PubSubTemplate pubSubTemplate) {
        this.pubSubTemplate = pubSubTemplate;
    }

    private final ObjectMapper objectMapper = new ObjectMapper();

    public void publishBookingStatus(BookingStatusMessage message) {
        try {
            String json = objectMapper.writeValueAsString(message);
            ByteString data = ByteString.copyFromUtf8(json);
            PubsubMessage pubsubMessage = PubsubMessage.newBuilder()
                .setData(data)
                .build();

            pubSubTemplate.publish("oms-booking-status", pubsubMessage);

        } catch (Exception e) {
            throw new RuntimeException("Failed to publish pubsub message", e);
        }
    }
}
