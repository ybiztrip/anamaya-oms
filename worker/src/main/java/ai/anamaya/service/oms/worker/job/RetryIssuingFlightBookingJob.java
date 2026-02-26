package ai.anamaya.service.oms.worker.job;

import ai.anamaya.service.oms.core.context.SystemCallerContext;
import ai.anamaya.service.oms.core.dto.request.BookingFlightListFilter;
import ai.anamaya.service.oms.core.enums.BookingFlightStatus;
import ai.anamaya.service.oms.core.service.BookingFlightService;
import ai.anamaya.service.oms.core.util.RedisLockManager;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class RetryIssuingFlightBookingJob {

    private final RedisLockManager redisLock;
    private final BookingFlightService bookingFlightService;

    @Scheduled(cron = "${cron.task.retry-issuing-flight-booking}")
    public void retryPendingBookingJob() {
        log.info("Running retry issuing flight booking scheduler...");

        int page = 0;
        int size = 50;

        BookingFlightListFilter filter = new BookingFlightListFilter();
        filter.setStatuses(List.of(
            BookingFlightStatus.ISSUING
        ));

        while (true) {

            var pageResult = bookingFlightService.getAll(page, size, "createdAt;asc", filter);

            pageResult.getContent().forEach(b -> {
                String bookingCode = b.getBookingCode();
                String lockKey = redisLock.bookingLockKey(bookingCode);

                try {
                    if (redisLock.acquireLock(lockKey, Duration.ofSeconds(30))) {
                        log.info("Booking issuing flight booked {} is already being processed. Skipping.", bookingCode);
                        return;
                    }

                    SystemCallerContext systemCallerContext = new SystemCallerContext(b.getCompanyId());
                    bookingFlightService.retryIssuingProcessBooking(systemCallerContext, b.getBookingId(), bookingCode);

                } catch (Exception ex) {
                    log.error("Error processing issuing flight booked {}", bookingCode, ex);

                } finally {
                    try {
                        redisLock.releaseLock(lockKey);
                    } catch (Exception ignored) {}
                }
            });

            if (pageResult.isLast()) {
                break;
            }

            page++;
        }

        log.info("Retry issuing flight booking scheduler completed.");
    }

}
