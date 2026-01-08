package ai.anamaya.service.oms.core.entity;

import ai.anamaya.service.oms.core.enums.BookingStatus;
import com.vladmihalcea.hibernate.type.json.JsonType;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import org.hibernate.annotations.Type;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.util.Map;

@Entity
@Table(name = "booking")
@Getter
@Setter
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public class Booking extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "company_id", nullable = false)
    private Long companyId;

    @Column(name = "code", nullable = false)
    private String code;

    @Column(name = "journey_code")
    private String journeyCode;

    @Column(name = "start_date")
    private LocalDate startDate;

    @Column(name = "end_date")
    private LocalDate endDate;

    @Column(name = "contact_email", nullable = false)
    private String contactEmail;

    @Column(name = "contact_first_name", nullable = false)
    private String contactFirstName;

    @Column(name = "contact_last_name", nullable = false)
    private String contactLastName;

    @Column(name = "contact_title", nullable = false)
    private String contactTitle;

    @Column(name = "contact_nationality", nullable = false)
    private String contactNationality;

    @Column(name = "contact_phone_code", nullable = false)
    private String contactPhoneCode;

    @Column(name = "contact_phone_number", nullable = false)
    private String contactPhoneNumber;

    @Column(name = "contact_dob")
    private LocalDate contactDob;

    @Type(JsonType.class)
    @Column(columnDefinition = "json")
    private Map<String, Object> additionalInfo;

    @Type(JsonType.class)
    @Column(columnDefinition = "json")
    private Map<String, Object> clientAdditionalInfo;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private BookingStatus status;

    @Column(name = "approved_by")
    private Long approvedBy;

    @Column(name = "approved_by_name")
    private String approvedByName;

    @Column(name = "rejected_by")
    private Long rejectedBy;

    @Column(name = "rejected_by_name")
    private String rejectedByName;
}
