package ai.anamaya.service.oms.core.entity;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;

@Entity
@Table(name = "company")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public class Company extends BaseEntity {

    @Column(nullable = false, length = 256)
    private String name;

    @Column(nullable = false)
    private Short status;

}
