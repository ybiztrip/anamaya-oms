package ai.anamaya.service.oms.consumer.pubsub;

import ai.anamaya.service.oms.dto.pubsub.BookingStatusMessage;
import ai.anamaya.service.oms.service.BookingStatusUpdateService;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import com.google.cloud.spring.pubsub.annotation.GcpPubSubSubscription;

@Slf4j
@Component
@RequiredArgsConstructor
public class BookingPubSubConsumer {

    private final ObjectMapper objectMapper;
    private final BookingStatusUpdateService bookingStatusUpdateService;

    @GcpPubSubSubscription("oms-booking-status-sub")
    public void onMessage(String messageJson) {
        try {
            log.info("Received Pub/Sub message: {}", messageJson);

            BookingStatusMessage msg =
                objectMapper.readValue(messageJson, BookingStatusMessage.class);

            bookingStatusUpdateService.handleBookingStatusUpdate(msg);

        } catch (Exception e) {
            log.error("Error processing Pub/Sub message", e);
        }
    }
}
