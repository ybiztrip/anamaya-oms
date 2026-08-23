package ai.anamaya.service.oms.core.client.internal.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class HotelOpenSearchResponse {

    private String id;
    private String name;
    private Integer star;
    private BigDecimal estimationPrice;
    private String address;
    private String province;
    private String city;
    private String countryCode;
    private String postalCode;
    private Double latitude;
    private Double longitude;
    private Integer rank;
    private String accommodationType;
}
