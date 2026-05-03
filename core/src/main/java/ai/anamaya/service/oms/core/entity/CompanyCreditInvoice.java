package ai.anamaya.service.oms.core.entity;

import ai.anamaya.service.oms.core.enums.CreditCodeType;
import ai.anamaya.service.oms.core.enums.InvoiceStatus;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

import java.math.BigDecimal;

@Entity
@Table(name = "company_credit_invoice")
@Getter
@Setter
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public class CompanyCreditInvoice extends BaseEntity {

    @Column(name = "company_id", nullable = false)
    private Long companyId;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private CreditCodeType code;

    private String docNo;

    @Column(nullable = false, precision = 18, scale = 2)
    private BigDecimal amount;

    @Column
    private String currency;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private InvoiceStatus status;
}
