package ai.anamaya.service.oms.core.entity;

import com.vladmihalcea.hibernate.type.json.JsonType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;
import org.hibernate.annotations.Type;

import java.util.List;
import java.util.Map;

@Entity
@Table(name = "travel_policy")
@Getter
@Setter
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public class TravelPolicy extends BaseEntity {

    @Column(name = "company_id", nullable = false)
    private Long companyId;

    @Column
    private String name;

    @Type(JsonType.class)
    @Column(name = "flights", columnDefinition = "jsonb")
    private List<Map<String, Object>> flights;

    @Column(name = "flight_minimum_price")
    private Integer flightMinimumPrice;

    @Column(name = "flight_maximum_price")
    private Integer flightMaximumPrice;

    @Column(name = "flight_minimum_class")
    private String flightMinimumClass;

    @Column(name = "flight_maximum_class")
    private String flightMaximumClass;

    @Column(name = "hotel_minimum_price")
    private Integer hotelMinimumPrice;

    @Column(name = "hotel_maximum_price")
    private Integer hotelMaximumPrice;

    @Column(name = "hotel_minimum_class")
    private String hotelMinimumClass;

    @Column(name = "hotel_maximum_class")
    private String hotelMaximumClass;

    @Column(name = "hotel_pagu")
    private String hotelPagu;

    @Column
    private Short status;

}
