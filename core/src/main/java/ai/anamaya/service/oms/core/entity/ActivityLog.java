package ai.anamaya.service.oms.core.entity;

import ai.anamaya.service.oms.core.enums.ActivityLogType;
import com.fasterxml.jackson.databind.JsonNode;
import com.vladmihalcea.hibernate.type.json.JsonType;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;
import org.hibernate.annotations.Type;

@Entity
@Table(name = "activity_log")
@Getter
@Setter
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public class ActivityLog extends BaseEntity {

    @Column(name = "company_id", nullable = false)
    private Long companyId;

    @Enumerated(EnumType.STRING)
    @Column(name = "type", nullable = false)
    private ActivityLogType type;

    @Column(name = "reference_id", nullable = false)
    private Long referenceId;

    @Type(JsonType.class)
    @Column(name = "data", columnDefinition = "jsonb")
    private JsonNode data;

    @Type(JsonType.class)
    @Column(name = "change_summary", columnDefinition = "json")
    private JsonNode changeSummary;

    @Column
    private Short status;

}
