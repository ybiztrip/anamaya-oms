package ai.anamaya.service.oms.worker.job;

import ai.anamaya.service.oms.core.context.CallerContext;
import ai.anamaya.service.oms.core.context.SystemCallerContext;
import ai.anamaya.service.oms.core.dto.request.BookingFlightListFilter;
import ai.anamaya.service.oms.core.entity.BookingFlight;
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
public class RetryFlightPendingBookingJob {

    private final RedisLockManager redisLock;
    private final BookingFlightService bookingFlightService;

    @Scheduled(cron = "${cron.task.retry-flight-pending-booking}")
    public void retryPendingBookingJob() {
        log.info("Running retry flight pending booking scheduler...");

        int page = 0;
        int size = 50;

        BookingFlightListFilter filter = new BookingFlightListFilter();
        filter.setStatuses(List.of(
            BookingFlightStatus.CREATED
        ));

        while (true) {

            var pageResult = bookingFlightService.getAll(page, size, "createdAt;asc", filter);

            pageResult.getContent().forEach(b -> {
                Long bookingId = b.getId();
                String lockKey = redisLock.bookingLockKey(bookingId);

                try {
                    if (redisLock.acquireLock(lockKey, Duration.ofSeconds(30))) {
                        log.info("Booking flight pending booked {} is already being processed. Skipping.", b.getBookingCode());
                        return;
                    }

                    CallerContext systemContext = new SystemCallerContext(b.getCompanyId());
                    BookingFlight bookingFlight = BookingFlight.builder()
                        .id(b.getId())
                        .bookingId(b.getBookingId())
                        .bookingCode(b.getBookingCode())
                        .companyId(b.getCompanyId())
                        .build();
                    bookingFlightService.retryBookingCreatedFlights(systemContext, bookingFlight);

                } catch (Exception ex) {
                    log.error("Error processing booking {}", bookingId, ex);

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

        log.info("Retry flight pending booking scheduler completed.");
    }


}
