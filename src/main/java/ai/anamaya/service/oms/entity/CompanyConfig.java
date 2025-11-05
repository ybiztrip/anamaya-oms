package ai.anamaya.service.oms.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(
    name = "company_config",
    uniqueConstraints = {
        @UniqueConstraint(columnNames = {"company_id", "code"})
    }
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CompanyConfig extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "company_id", nullable = false)
    private Long companyId;

    @Column(name = "code", nullable = false, length = 100)
    private String code;

    @Column(name = "value_str", length = 256)
    private String valueStr;

    @Column(name = "value_int")
    private Integer valueInt;

    @Column(name = "value_bool")
    private Boolean valueBool;

    @Column(nullable = false)
    private Short status;

    @Column(name = "is_visible", nullable = false)
    private Short isVisible;
}
