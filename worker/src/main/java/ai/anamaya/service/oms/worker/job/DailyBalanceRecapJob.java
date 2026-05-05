package ai.anamaya.service.oms.worker.job;

import ai.anamaya.service.oms.core.service.BalanceRecapDailyService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDate;

@Slf4j
@Component
@RequiredArgsConstructor
public class DailyBalanceRecapJob {

    private final BalanceRecapDailyService balanceRecapDailyService;

    @Scheduled(cron = "${cron.task.daily-balance-recap}")
    public void runDailyBalanceRecap() {
        LocalDate yesterday = LocalDate.now().minusDays(1);
        log.info("Running daily balance recap for date: {}", yesterday);
        try {
            var results = balanceRecapDailyService.recapDailyBalance(yesterday);
            log.info("Daily balance recap completed. Records saved: {}", results.size());
        } catch (Exception ex) {
            log.error("Daily balance recap failed for date: {}", yesterday, ex);
        }
    }
}