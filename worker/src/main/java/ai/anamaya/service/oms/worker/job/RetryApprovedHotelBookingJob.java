package ai.anamaya.service.oms.worker.job;

import ai.anamaya.service.oms.core.context.SystemCallerContext;
import ai.anamaya.service.oms.core.dto.request.BookingHotelListFilter;
import ai.anamaya.service.oms.core.enums.BookingHotelStatus;
import ai.anamaya.service.oms.core.service.BookingHotelService;
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
public class RetryApprovedHotelBookingJob {

    private final RedisLockManager redisLock;
    private final BookingHotelService bookingHotelService;

    @Scheduled(cron = "${cron.task.retry-approved-hotel-booking}")
    public void retryApprovedHotelBookingJob() {
        log.info("Running retry approved hotel booking scheduler...");

        int page = 0;
        int size = 50;

        BookingHotelListFilter filter = new BookingHotelListFilter();
        filter.setStatuses(List.of(BookingHotelStatus.APPROVED));

        while (true) {

            var pageResult = bookingHotelService.getAll(page, size, "createdAt;asc", filter);

            pageResult.getContent().forEach(b -> {
                String bookingCode = b.getBookingCode();
                String lockKey = redisLock.bookingLockKey(bookingCode);

                try {
                    if (redisLock.acquireLock(lockKey, Duration.ofSeconds(30))) {
                        log.info("Booking approved hotel booked{} is already being processed. Skipping.", bookingCode);
                        return;
                    }

                    SystemCallerContext systemCallerContext = new SystemCallerContext(b.getCompanyId());
                    bookingHotelService.retryApproveProcessBooking(systemCallerContext, b.getBookingId(), b.getBookingCode());

                } catch (Exception ex) {
                    log.error("Error processing approved flight booked {}", bookingCode, ex);

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
