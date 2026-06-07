package ai.anamaya.service.oms.core.entity;

import ai.anamaya.service.oms.core.enums.BalanceSourceType;
import ai.anamaya.service.oms.core.enums.BalanceTransactionType;
import ai.anamaya.service.oms.core.enums.BookingType;
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

    @Column(name = "contact_email")
    private String contactEmail;

    @Column(name = "triggered_by_email")
    private String triggeredByEmail;

    @Enumerated(EnumType.STRING)
    @Column(name = "source_type")
    private BalanceSourceType sourceType; // e.g., Booking, Procurement

    @Enumerated(EnumType.STRING)
    @Column(name = "booking_type")
    private BookingType bookingType; // FLIGHT or HOTEL when sourceType=BOOKING; null otherwise

    @Enumerated(EnumType.STRING)
    @Column(name = "type")
    private BalanceTransactionType type; // 1 = CREDIT, 2 = DEBIT

    @Column(name = "amount", precision = 18, scale = 2)
    private BigDecimal amount;

    @Column(name = "begin_balance", precision = 18, scale = 2)
    private BigDecimal beginBalance;

    @Column(name = "end_balance", precision = 18, scale = 2)
    private BigDecimal endBalance;

    @Column(name = "remarks")
    private String remarks;
}
