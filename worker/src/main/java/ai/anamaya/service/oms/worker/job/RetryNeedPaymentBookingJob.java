package ai.anamaya.service.oms.worker.job;

import ai.anamaya.service.oms.core.dto.request.BookingListFilter;
import ai.anamaya.service.oms.core.enums.BookingStatus;
import ai.anamaya.service.oms.core.service.BookingApproveService;
import ai.anamaya.service.oms.core.service.BookingService;
import ai.anamaya.service.oms.core.util.RedisLockManager;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class RetryNeedPaymentBookingJob {

    @Autowired
    private RedisLockManager redisLock;

    private final BookingService bookingService;
    private final BookingApproveService bookingApproveService;

    @Scheduled(cron = "${cron.task.retry-pending-order}")
    public void checkBookings() {
        log.info("Running booking scheduler...");

        int page = 0;
        int size = 50;

        BookingListFilter filter = new BookingListFilter();
        filter.setStatuses(List.of(BookingStatus.ON_PROCESS));

        while (true) {

            var pageResult = bookingService.getAll(page, size, "createdAt;asc", filter);

            pageResult.getContent().forEach(b -> {
                Long bookingId = b.getId();
                String lockKey = redisLock.bookingLockKey(bookingId);

                try {
                    if (!redisLock.acquireLock(lockKey, Duration.ofSeconds(30))) {
                        log.warn("Booking {} is already being processed. Skipping.", bookingId);
                        return;
                    }

                    bookingApproveService.retryApproveConfirmBooking(bookingId);

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

        log.info("Booking scheduler completed.");
    }


}
