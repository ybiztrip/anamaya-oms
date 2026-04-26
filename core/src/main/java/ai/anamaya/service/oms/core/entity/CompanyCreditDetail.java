package ai.anamaya.service.oms.core.entity;

import ai.anamaya.service.oms.core.enums.CreditSourceType;
import ai.anamaya.service.oms.core.enums.CreditTransactionType;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

import java.math.BigDecimal;

@Entity
@Table(name = "company_credit_detail")
@Getter
@Setter
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public class CompanyCreditDetail extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "balance_id")
    private CompanyCredit balance;

    @Column(name = "reference_id")
    private Long referenceId;

    @Column(name = "reference_code")
    private String referenceCode;

    @Enumerated(EnumType.STRING)
    @Column(name = "source_type")
    private CreditSourceType sourceType; // e.g., Booking, Procurement

    @Enumerated(EnumType.STRING)
    @Column(name = "type")
    private CreditTransactionType type; // 1 = CREDIT, 2 = DEBIT

    @Column(name = "amount", precision = 18, scale = 2)
    private BigDecimal amount;

    @Column(name = "begin_balance", precision = 18, scale = 2)
    private BigDecimal beginBalance;

    @Column(name = "end_balance", precision = 18, scale = 2)
    private BigDecimal endBalance;

    @Column(name = "remarks")
    private String remarks;
}
