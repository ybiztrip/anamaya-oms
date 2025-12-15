package ai.anamaya.service.oms.core.entity;

import ai.anamaya.service.oms.core.enums.BookingFlightStatus;
import com.vladmihalcea.hibernate.type.json.JsonType;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;
import org.hibernate.annotations.Type;

@Entity
@Table(name = "booking_flight_history")
@Getter
@Setter
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public class BookingFlightHistory extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "booking_id")
    private Long bookingId;

    @Column(name = "booking_code")
    private String  bookingCode;

    @Enumerated(EnumType.STRING)
    @Column(name = "status")
    private BookingFlightStatus status;

    @Type(JsonType.class)
    @Column(name = "data", columnDefinition = "json")
    private Object data;
}
