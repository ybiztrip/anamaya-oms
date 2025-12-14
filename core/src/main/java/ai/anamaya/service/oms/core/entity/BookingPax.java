package ai.anamaya.service.oms.core.entity;

import ai.anamaya.service.oms.core.enums.PaxGender;
import ai.anamaya.service.oms.core.enums.PaxTitle;
import ai.anamaya.service.oms.core.enums.PaxType;
import com.vladmihalcea.hibernate.type.json.JsonType;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;
import org.hibernate.annotations.Type;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

@Getter
@Setter
@Entity
@Table(name = "booking_pax")
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public class BookingPax extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "booking_id", nullable = false)
    private Long bookingId;

    @Column(name = "booking_code", nullable = false)
    private String bookingCode;

    @Column(name = "email")
    private String email;

    @Column(name = "first_name", nullable = false)
    private String firstName;

    @Column(name = "last_name", nullable = false)
    private String lastName;

    @Enumerated(EnumType.STRING)
    @Column(name = "gender", nullable = false)
    private PaxGender gender;

    @Enumerated(EnumType.STRING)
    @Column(name = "type", nullable = false)
    private PaxType type;

    @Enumerated(EnumType.STRING)
    @Column(name = "title", nullable = false)
    private PaxTitle title;

    @Column(name = "nationality")
    private String nationality;

    @Column(name = "phone_code")
    private String phoneCode;

    @Column(name = "phone_number")
    private String phoneNumber;

    @Column(name = "dob")
    private LocalDate dob;

    @Type(JsonType.class)
    @Column(name = "add_on", columnDefinition = "json")
    private List<Map<String, Object>> addOn;

    @Column(name = "issuing_country")
    private String issuingCountry;

    @Column(name = "document_type")
    private String documentType;

    @Column(name = "document_no")
    private String documentNo;

    @Column(name = "expiration_date")
    private LocalDate expirationDate;
}
