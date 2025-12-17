package ai.anamaya.service.oms.worker.job;

import ai.anamaya.service.oms.core.context.SystemCallerContext;
import ai.anamaya.service.oms.core.dto.request.BookingListFilter;
import ai.anamaya.service.oms.core.enums.BookingStatus;
import ai.anamaya.service.oms.core.service.BookingHotelService;
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
public class RetryApprovedHotelBookingJob {

    @Autowired
    private RedisLockManager redisLock;

    private final BookingHotelService bookingHotelService;

    @Scheduled(cron = "${cron.task.retry-approved-hotel-booking}")
    public void retryApprovedHotelBookingJob() {
        log.info("Running retry approved hotel booking scheduler...");

        int page = 0;
        int size = 50;

        BookingListFilter filter = new BookingListFilter();
        filter.setStatuses(List.of(BookingStatus.APPROVED));

        while (true) {

            var pageResult = bookingHotelService.getAll(page, size, "createdAt;asc", filter);

            pageResult.getContent().forEach(b -> {
                Long bookingId = b.getId();
                String lockKey = redisLock.bookingLockKey(bookingId);

                try {
                    if (redisLock.acquireLock(lockKey, Duration.ofSeconds(30))) {
                        log.warn("Booking {} is already being processed. Skipping.", bookingId);
                        return;
                    }

                    SystemCallerContext systemCallerContext = new SystemCallerContext(b.getCompanyId());
                    bookingHotelService.retryApproveProcessBooking(systemCallerContext, b.getBookingId(), b.getBookingCode());

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

        log.info("Retry approved hotel Booking scheduler completed.");
    }


}
