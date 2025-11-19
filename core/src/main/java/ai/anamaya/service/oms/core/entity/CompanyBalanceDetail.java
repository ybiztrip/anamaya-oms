package ai.anamaya.service.oms.core.entity;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;

import java.math.BigDecimal;

@Entity
@Table(name = "company_balance_detail")
@Getter
@Setter
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public class CompanyBalanceDetail extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "balance_id")
    private CompanyBalance balance;

    @Column(name = "reference_id")
    private Long referenceId;

    @Column(name = "reference_code")
    private String referenceCode;

    @Column(name = "source_type")
    private Short sourceType; // e.g., Booking, Procurement

    @Column(name = "type")
    private Short type; // 1 = CREDIT, 2 = DEBIT

    @Column(name = "amount", precision = 18, scale = 2)
    private BigDecimal amount;

    @Column(name = "begin_balance", precision = 18, scale = 2)
    private BigDecimal beginBalance;

    @Column(name = "end_balance", precision = 18, scale = 2)
    private BigDecimal endBalance;

    @Column(name = "remarks")
    private String remarks;
}
