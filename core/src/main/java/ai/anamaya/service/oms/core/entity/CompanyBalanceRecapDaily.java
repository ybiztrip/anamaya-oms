package ai.anamaya.service.oms.core.entity;

import ai.anamaya.service.oms.core.enums.BalanceCodeType;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;

import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@Table(name = "company_balance_recap_daily")
@Getter
@Setter
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public class CompanyBalanceRecapDaily extends BaseEntity {

    @Column(name = "company_id", nullable = false)
    private Long companyId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "balance_id")
    private CompanyBalance balance;

    @Enumerated(EnumType.STRING)
    @Column(name = "code", nullable = false)
    private BalanceCodeType code;

    @Column(name = "recap_date", nullable = false)
    private LocalDate recapDate;

    @Column(name = "begin_balance", nullable = false, precision = 18, scale = 2)
    private BigDecimal beginBalance;

    @Column(name = "end_balance", nullable = false, precision = 18, scale = 2)
    private BigDecimal endBalance;

    @Column(name = "currency")
    private String currency;
}