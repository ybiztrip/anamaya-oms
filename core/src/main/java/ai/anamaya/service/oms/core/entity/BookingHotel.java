package ai.anamaya.service.oms.core.entity;

import ai.anamaya.service.oms.core.enums.BookingHotelStatus;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;

import java.time.LocalDate;

@Entity
@Table(name = "booking_hotel")
@Getter
@Setter
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public class BookingHotel extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "booking_id")
    private Long bookingId;

    @Column(name = "client_source", length = 50)
    private String clientSource;

    @Column(name = "item_id", length = 256, nullable = false)
    private String itemId;

    @Column(name = "room_id", length = 256, nullable = false)
    private String roomId;

    @Column(name = "rate_key", columnDefinition = "TEXT")
    private String rateKey;

    @Column(name = "payment_key", columnDefinition = "TEXT")
    private String paymentKey;

    @Column(name = "num_room")
    private Short numRoom;

    @Column(name = "check_in_date")
    private LocalDate checkInDate;

    @Column(name = "check_out_date")
    private LocalDate checkOutDate;

    @Column(name = "partner_sell_amount")
    private Double partnerSellAmount;

    @Column(name = "partner_nett_amount")
    private Double partnerNettAmount;

    @Column(name = "currency", length = 4)
    private String currency;

    @Column(name = "special_request", columnDefinition = "TEXT")
    private String specialRequest;

    @Column(name = "booking_reference", length = 256)
    private String bookingReference;

    @Enumerated(EnumType.STRING)
    @Column(name = "status")
    private BookingHotelStatus status;
}
