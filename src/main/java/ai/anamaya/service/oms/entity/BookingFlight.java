package ai.anamaya.service.oms.entity;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "booking_flight")
@Getter
@Setter
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public class BookingFlight extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "booking_id")
    private Long bookingId;

    @Column(name = "type")
    private Short type;

    @Column(name = "client_source", length = 50)
    private String clientSource;

    @Column(name = "item_id", length = 256)
    private String itemId;

    @Column(name = "origin", length = 100)
    private String origin;

    @Column(name = "destination", length = 100)
    private String destination;

    @Column(name = "departure_datetime")
    private LocalDateTime departureDatetime;

    @Column(name = "arrival_datetime")
    private LocalDateTime arrivalDatetime;

    @Column(name = "adult_amount", precision = 18, scale = 2)
    private BigDecimal adultAmount;

    @Column(name = "child_amount", precision = 18, scale = 2)
    private BigDecimal childAmount;

    @Column(name = "infant_amount", precision = 18, scale = 2)
    private BigDecimal infantAmount;

    @Column(name = "total_amount", precision = 18, scale = 2)
    private BigDecimal totalAmount;

    @Column(name = "status")
    private Short status;
}
