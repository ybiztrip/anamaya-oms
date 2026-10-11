package ai.anamaya.service.oms.worker.job;

import ai.anamaya.service.oms.core.context.SystemCallerContext;
import ai.anamaya.service.oms.core.entity.BookingTrain;
import ai.anamaya.service.oms.core.enums.BookingTrainStatus;
import ai.anamaya.service.oms.core.service.BookingTrainService;
import ai.anamaya.service.oms.core.util.RedisLockManager;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.util.List;
import java.util.function.Consumer;

@Slf4j
@Component
@RequiredArgsConstructor
public class TrainBookingJob {

    private final RedisLockManager redisLock;
    private final BookingTrainService bookingTrainService;

    // Pub/Sub is published before commit, so the consumer can miss APPROVED rows; pick them up here
    @Scheduled(cron = "${cron.task.retry-approved-train-booking:0 */1 * * * *}")
    public void retryApproved() {
        run("retry approved", bookingTrainService.findByStatus(BookingTrainStatus.APPROVED), t ->
            bookingTrainService.retryApproveProcessBooking(new SystemCallerContext(t.getCompanyId()), t.getBookingId(), t.getBookingCode()));
    }

    @Scheduled(cron = "${cron.task.retry-issuing-train-booking:30 */1 * * * *}")
    public void retryIssuing() {
        run("retry issuing", bookingTrainService.findByStatus(BookingTrainStatus.ISSUING), t ->
            bookingTrainService.retryIssuingProcessBooking(new SystemCallerContext(t.getCompanyId()), t.getBookingId(), t.getBookingCode()));
    }

    @Scheduled(cron = "${cron.task.expire-train-booking:0 */1 * * * *}")
    public void expire() {
        run("expire", bookingTrainService.findExpired(), t -> bookingTrainService.expire(t.getId()));
    }

    private void run(String name, List<BookingTrain> trains, Consumer<BookingTrain> action) {
        log.info("Running {} train booking scheduler for {} rows", name, trains.size());

        for (BookingTrain train : trains) {
            String lockKey = redisLock.bookingLockKey(train.getBookingCode());

            // acquireLock returns true when the lock is already held
            if (redisLock.acquireLock(lockKey, Duration.ofSeconds(30))) {
                log.info("Train booking {} is already being processed. Skipping.", train.getBookingCode());
                continue;
            }

            try {
                action.accept(train);
            } catch (Exception ex) {
                log.error("Error on {} train booking {}", name, train.getBookingCode(), ex);
            } finally {
                try {
                    redisLock.releaseLock(lockKey);
                } catch (Exception ignored) {}
            }
        }
    }
}
