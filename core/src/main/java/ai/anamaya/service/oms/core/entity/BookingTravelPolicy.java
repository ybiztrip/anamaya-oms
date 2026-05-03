package ai.anamaya.service.oms.core.entity;

import ai.anamaya.service.oms.core.enums.BookingType;
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
@Table(name = "booking_travel_policy")
@Getter
@Setter
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public class BookingTravelPolicy extends BaseEntity {

    @Column(name = "company_id", nullable = false)
    private Long companyId;

    @Enumerated(EnumType.STRING)
    @Column(name = "type", nullable = false)
    private BookingType type;

    @Column(name = "booking_code", nullable = false)
    private String bookingCode;

    @Column(name = "travel_policy_id", nullable = false)
    private Long travelPolicyId;

    @Column(name = "travel_policy_name", nullable = false)
    private String travelPolicyName;

    @Type(JsonType.class)
    @Column(name = "data", columnDefinition = "jsonb")
    private JsonNode data;

}
