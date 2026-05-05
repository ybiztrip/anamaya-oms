package ai.anamaya.service.oms.core.service;

import ai.anamaya.service.oms.core.dto.response.BalanceRecapDailyResponse;
import ai.anamaya.service.oms.core.entity.CompanyBalance;
import ai.anamaya.service.oms.core.entity.CompanyBalanceDetail;
import ai.anamaya.service.oms.core.entity.CompanyBalanceRecapDaily;
import ai.anamaya.service.oms.core.repository.CompanyBalanceDetailRepository;
import ai.anamaya.service.oms.core.repository.CompanyBalanceRecapDailyRepository;
import ai.anamaya.service.oms.core.repository.CompanyBalanceRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class BalanceRecapDailyService {

    private final CompanyBalanceRepository balanceRepository;
    private final CompanyBalanceDetailRepository detailRepository;
    private final CompanyBalanceRecapDailyRepository recapRepository;

    @Transactional
    public List<BalanceRecapDailyResponse> recapDailyBalance(LocalDate date) {
        List<CompanyBalance> allBalances = balanceRepository.findAll();
        List<BalanceRecapDailyResponse> results = new ArrayList<>();

        for (CompanyBalance balance : allBalances) {
            List<CompanyBalanceDetail> details = detailRepository.findByBalanceIdAndDate(balance.getId(), date);

            BigDecimal beginBalance;
            BigDecimal endBalance;

            if (!details.isEmpty()) {
                beginBalance = details.get(0).getBeginBalance();
                endBalance = details.get(details.size() - 1).getEndBalance();
            } else {
                // fallback 1: carry forward end_balance from latest existing recap before this date
                Optional<CompanyBalanceRecapDaily> latestRecap = recapRepository
                        .findLatestBeforeDate(balance.getId(), date, PageRequest.of(0, 1))
                        .stream().findFirst();

                if (latestRecap.isPresent()) {
                    beginBalance = latestRecap.get().getEndBalance();
                    endBalance = latestRecap.get().getEndBalance();
                } else {
                    // fallback 2: derive from latest detail row ever recorded before this date
                    Optional<CompanyBalanceDetail> latestDetail = detailRepository
                            .findLatestBeforeDate(balance.getId(), date, PageRequest.of(0, 1))
                            .stream().findFirst();

                    if (latestDetail.isPresent()) {
                        beginBalance = latestDetail.get().getEndBalance();
                        endBalance = latestDetail.get().getEndBalance();
                    } else {
                        // no data at all for this balance — skip
                        continue;
                    }
                }
            }

            CompanyBalanceRecapDaily recap = recapRepository
                    .findByBalanceIdAndRecapDate(balance.getId(), date)
                    .orElse(CompanyBalanceRecapDaily.builder()
                            .companyId(balance.getCompanyId())
                            .balance(balance)
                            .code(balance.getCode())
                            .recapDate(date)
                            .currency(balance.getCurrency())
                            .build());

            recap.setBeginBalance(beginBalance);
            recap.setEndBalance(endBalance);

            recapRepository.save(recap);
            results.add(toResponse(recap));
        }

        log.info("Daily balance recap for {} completed. Records saved: {}", date, results.size());
        return results;
    }

    private BalanceRecapDailyResponse toResponse(CompanyBalanceRecapDaily recap) {
        return BalanceRecapDailyResponse.builder()
                .id(recap.getId())
                .companyId(recap.getCompanyId())
                .code(recap.getCode())
                .recapDate(recap.getRecapDate())
                .beginBalance(recap.getBeginBalance())
                .endBalance(recap.getEndBalance())
                .currency(recap.getCurrency())
                .createdAt(recap.getCreatedAt())
                .updatedAt(recap.getUpdatedAt())
                .build();
    }
}