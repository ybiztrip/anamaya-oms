package ai.anamaya.service.oms.core.entity;

import ai.anamaya.service.oms.core.enums.BookingPaymentMethod;
import ai.anamaya.service.oms.core.enums.BookingTrainStatus;
import com.vladmihalcea.hibernate.type.json.JsonType;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;
import org.hibernate.annotations.Type;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@Entity
@Table(name = "booking_train")
@Getter
@Setter
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public class BookingTrain extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "company_id")
    private Long companyId;

    @Column(name = "booking_id")
    private Long bookingId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "booking_id", insertable = false, updatable = false)
    private Booking booking;

    @Column(name = "booking_code")
    private String bookingCode;

    @Column(name = "class_key")
    private String classKey;

    @Column(name = "journey_code")
    private String journeyCode;

    @Column(name = "carrier_number")
    private String carrierNumber;

    @Column(name = "train_name")
    private String trainName;

    @Column(name = "sub_class")
    private String subClass;

    @Column(name = "seat_class")
    private String seatClass;

    @Column(name = "provider")
    private Integer provider;

    @Column(name = "origin")
    private String origin;

    @Column(name = "destination")
    private String destination;

    @Column(name = "departure_datetime")
    private LocalDateTime departureDatetime;

    @Column(name = "arrival_datetime")
    private LocalDateTime arrivalDatetime;

    @Column(name = "total_amount", precision = 18, scale = 2)
    private BigDecimal totalAmount;

    @Column(name = "management_fee_amount", precision = 18, scale = 2)
    private BigDecimal managementFeeAmount;

    @Enumerated(EnumType.STRING)
    @Column(name = "payment_method")
    private BookingPaymentMethod paymentMethod;

    // Biztrip booking id, used for seat-map / set-seat / issue / detail
    @Column(name = "booking_reference")
    private String bookingReference;

    // Biztrip referenceCode, e.g. QKV9REJ_13
    @Column(name = "reference_code")
    private String referenceCode;

    // KAI booking code, e.g. QKV9REJ
    @Column(name = "ota_reference")
    private String otaReference;

    // Biztrip reservation deadline, WIB wall time
    @Column(name = "time_limit")
    private LocalDateTime timeLimit;

    // [{paxId, coachName, seatNumber}]
    @Type(JsonType.class)
    @Column(name = "seats", columnDefinition = "json")
    private List<Map<String, Object>> seats;

    @Enumerated(EnumType.STRING)
    @Column(name = "status")
    private BookingTrainStatus status;

    @Column(name = "approved_by")
    private Long approvedBy;

    @Column(name = "approved_by_email")
    private String approvedByEmail;

    @Column(name = "approved_at")
    private LocalDateTime approvedAt;

    @Column(name = "rejected_by")
    private Long rejectedBy;

    @Column(name = "rejected_by_email")
    private String rejectedByEmail;

    @Column(name = "rejected_at")
    private LocalDateTime rejectedAt;

    @Column(columnDefinition = "json")
    private String metadata;

    @Column(name = "error_message", columnDefinition = "TEXT")
    private String errorMessage;
}
