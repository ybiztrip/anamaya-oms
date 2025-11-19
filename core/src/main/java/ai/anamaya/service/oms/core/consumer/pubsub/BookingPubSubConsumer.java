package ai.anamaya.service.oms.core.consumer.pubsub;

import ai.anamaya.service.oms.core.dto.pubsub.BookingStatusMessage;
import ai.anamaya.service.oms.core.service.BookingStatusUpdateService;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.integration.annotation.ServiceActivator;
import org.springframework.messaging.Message;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class BookingPubSubConsumer {

    private final ObjectMapper mapper;
    private final BookingStatusUpdateService bookingStatusUpdateService;

    @ServiceActivator(inputChannel = "bookingStatusInputChannel")
    public void receiveMessage(Message<String> message) {
        try {
            String json = message.getPayload();
            log.info("Received Pub/Sub message: {}", json);

            BookingStatusMessage msg =
                mapper.readValue(json, BookingStatusMessage.class);

            bookingStatusUpdateService.handleBookingStatusUpdate(msg);

        } catch (Exception e) {
            log.error("Error processing Pub/Sub message", e);
        }
    }
}
