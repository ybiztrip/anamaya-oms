package ai.anamaya.service.oms.core.entity;

import ai.anamaya.service.oms.core.enums.ApprovalAction;
import ai.anamaya.service.oms.core.enums.BookingType;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;

@Entity
@Table(name = "booking_approval")
@Getter
@Setter
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public class BookingApproval extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "booking_id")
    private Long bookingId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "booking_id", insertable = false, updatable = false)
    private Booking booking;

    @Enumerated(EnumType.STRING)
    @Column(name = "booking_type", nullable = false, length = 20)
    private BookingType bookingType;

    @Column(name = "booking_child_id")
    private Long bookingChildId;

    @Enumerated(EnumType.STRING)
    @Column(name = "action", nullable = false, length = 20)
    private ApprovalAction action;

    @Column(name = "created_by_name")
    private String createdByName;

    @Column(name = "notes", columnDefinition = "TEXT")
    private String notes;
}