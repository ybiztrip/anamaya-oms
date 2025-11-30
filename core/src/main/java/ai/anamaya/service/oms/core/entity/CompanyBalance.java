package ai.anamaya.service.oms.core.entity;

import ai.anamaya.service.oms.core.enums.BalanceCodeType;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;

import java.math.BigDecimal;

@Entity
@Table(name = "company_balance")
@Getter
@Setter
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public class CompanyBalance extends BaseEntity {

    @Column(name = "company_id", nullable = false)
    private Long companyId;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private BalanceCodeType code;

    @Column(nullable = false, precision = 18, scale = 2)
    private BigDecimal balance;

    @Column
    private String currency;

    @Column
    private Short status;
}
