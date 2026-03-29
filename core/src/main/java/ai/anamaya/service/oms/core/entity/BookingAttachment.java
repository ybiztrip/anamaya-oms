package ai.anamaya.service.oms.core.entity;

import ai.anamaya.service.oms.core.enums.BookingType;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

@Entity
@Table(name = "booking_attachment")
@Setter
@Getter
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public class BookingAttachment extends BaseEntity{

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "company_id")
    private Long companyId;

    @Column(name = "booking_id")
    private Long bookingId;

    @Column(name = "booking_code")
    private String bookingCode;

    @Enumerated(EnumType.STRING)
    @Column(name = "type")
    private BookingType type;

    @Column(name = "file")
    private String file;

}
