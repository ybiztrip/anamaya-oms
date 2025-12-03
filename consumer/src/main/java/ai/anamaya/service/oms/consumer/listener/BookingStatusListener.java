package ai.anamaya.service.oms.consumer.listener;

import ai.anamaya.service.oms.core.context.CallerContext;
import ai.anamaya.service.oms.core.context.SystemCallerContext;
import ai.anamaya.service.oms.core.dto.pubsub.BookingStatusMessage;
import ai.anamaya.service.oms.core.service.BookingApproveService;
import ai.anamaya.service.oms.core.service.BookingService;
import ai.anamaya.service.oms.core.util.RedisLockManager;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.cloud.spring.pubsub.core.subscriber.PubSubSubscriberTemplate;
import com.google.cloud.spring.pubsub.support.BasicAcknowledgeablePubsubMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.time.Duration;

@Slf4j
@Service
@RequiredArgsConstructor
public class BookingStatusListener {

    @Autowired
    private RedisLockManager redisLock;

    private final BookingService bookingService;
    private final BookingApproveService bookingApproveService;
    private final PubSubSubscriberTemplate subscriberTemplate;
    private final ObjectMapper objectMapper = new ObjectMapper();

    private static final String SUBSCRIPTION = "oms-booking-status-sub";

    @PostConstruct
    public void subscribe() {
        subscriberTemplate.subscribe(SUBSCRIPTION, this::handleMessage);
        log.info("Subscribed to Pub/Sub: {}", SUBSCRIPTION);
    }

    private void handleMessage(BasicAcknowledgeablePubsubMessage message) {
        Long bookingId = null;

        try {
            String data = message.getPubsubMessage().getData().toStringUtf8();
            log.info("Received PubSub message: {}", data);

            BookingStatusMessage bookingStatus =
                objectMapper.readValue(data, BookingStatusMessage.class);

            bookingId = bookingStatus.getBookingId();
            String lockKey = redisLock.bookingLockKey(bookingId);
            if (!redisLock.acquireLock(lockKey, Duration.ofSeconds(30))) {
                log.warn("Booking {} is already being processed. Skipping.", bookingId);
                message.nack();
                return;
            }

            CallerContext systemContext = new SystemCallerContext(bookingStatus.getCompanyId());

            switch (bookingStatus.getStatus()) {
                case APPROVED:
                    bookingApproveService.approveConfirmBooking(systemContext, bookingStatus.getBookingId());
                    break;
                case CANCELLED:
                    break;
                default:
            }

            message.ack();
        } catch (Exception e) {
            log.error("Error processing message", e);
            message.nack();
        } finally {
            try {
                if (bookingId != null) {
                    redisLock.releaseLock(redisLock.bookingLockKey(bookingId));
                }
            } catch (Exception ignored) {}
        }
    }
}
