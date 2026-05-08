package ai.anamaya.service.oms.core.service;

import ai.anamaya.service.oms.core.entity.CompanyConfig;
import ai.anamaya.service.oms.core.repository.CompanyConfigRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;

@Slf4j
@Service
@RequiredArgsConstructor
public class ManagementFeeService {

    public static final String CONFIG_FLIGHT = "CREDIT_FLIGHT";
    public static final String CONFIG_HOTEL = "CREDIT_HOTEL";

    private static final BigDecimal HUNDRED = new BigDecimal("100");

    private final CompanyConfigRepository companyConfigRepository;

    public BigDecimal calculateFlightFee(Long companyId, BigDecimal totalAmount) {
        if (totalAmount == null) {
            throw new IllegalArgumentException("Flight total_amount must not be null");
        }
        return calculate(companyId, CONFIG_FLIGHT, totalAmount);
    }

    public BigDecimal calculateHotelFee(Long companyId, BigDecimal partnerNettAmount) {
        if (partnerNettAmount == null) {
            throw new IllegalArgumentException("Hotel partner_nett_amount must not be null");
        }
        return calculate(companyId, CONFIG_HOTEL, partnerNettAmount);
    }

    public void validateConfig(Long companyId, String code) {
        CompanyConfig cfg = companyConfigRepository.findByCompanyIdAndCode(companyId, code)
            .orElseThrow(() -> new IllegalStateException(
                "Company config '" + code + "' not found for this company"));

        BigDecimal pct = parsePercentage(cfg.getValueStr(), code, companyId);
        if (pct.signum() < 0) {
            throw new IllegalStateException(
                "Company config '" + code + "' has negative percentage for this company");
        }
    }

    private BigDecimal calculate(Long companyId, String code, BigDecimal base) {
        CompanyConfig cfg = companyConfigRepository.findByCompanyIdAndCode(companyId, code)
            .orElseThrow(() -> new IllegalStateException(
                "Company config '" + code + "' not found for this company"));

        BigDecimal pct = parsePercentage(cfg.getValueStr(), code, companyId);
        if (pct.signum() < 0) {
            throw new IllegalStateException(
                "Company config '" + code + "' has negative percentage for this company");
        }

        return base.multiply(pct)
            .divide(HUNDRED, 2, RoundingMode.HALF_UP);
    }

    private BigDecimal parsePercentage(String raw, String code, Long companyId) {
        if (raw == null || raw.isBlank()) {
            throw new IllegalStateException(
                "Company config '" + code + "' has empty value_str for this company");
        }
        try {
            return new BigDecimal(raw.trim());
        } catch (NumberFormatException ex) {
            throw new IllegalStateException(
                "Company config '" + code + "' value_str is not numeric for this company", ex);
        }
    }
}
