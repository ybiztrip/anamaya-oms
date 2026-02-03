package ai.anamaya.service.oms.core.client.biztrip.mapper.response;

import ai.anamaya.service.oms.core.client.biztrip.dto.hotel.response.BiztripHotelRoomRateResponse;
import ai.anamaya.service.oms.core.dto.response.HotelRoomRateResponse;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class BiztripHotelRoomRateResponseMapper {

    private final ObjectMapper mapper = new ObjectMapper();

    public List<HotelRoomRateResponse> map(Boolean isSuccess,
                                           List<BiztripHotelRoomRateResponse> source) {

        if (Boolean.FALSE.equals(isSuccess) || source == null) {
            return Collections.emptyList();
        }

        return source.stream()
            .map(this::mapItem)
            .collect(Collectors.toList());
    }

    private HotelRoomRateResponse mapItem(BiztripHotelRoomRateResponse src) {

        return HotelRoomRateResponse.builder()
            // ===== BASIC =====
            .rateStatus(src.getRateStatus())
            .propertyId(src.getPropertyId())
            .providerRoomId(src.getProviderRoomId())
            .roomId(src.getRoomId())
            .roomName(src.getRoomName())
            .roomType(src.getRoomType())
            .checkInDate(src.getCheckInDate())
            .checkOutDate(src.getCheckOutDate())
            .numRooms(src.getNumRooms())
            .numAdults(src.getNumAdults())
            .numChildren(src.getNumChildren())
            .maxOccupancy(src.getMaxOccupancy())
            .mealType(src.getMealType())
            .rateKey(src.getRateKey())

            // ===== PRICING =====
            .totalRates(toMap(src.getTotalRates()))
            .nightlyRates(toMap(src.getNightlyRates()))
            .charges(toListMap(src.getCharges()))
            .ratesPerDay(toListMap(src.getRatesPerDay()))

            // ===== POLICIES =====
            .cancellationPolicy(src.getCancellationPolicy())
            .checkInPolicy(src.getCheckInPolicy())
            .occupancyPricing(src.getOccupancyPricing())

            // ===== ROOM DETAILS =====
            .bedArrangement(toListMap(src.getBedArrangement()))
            .roomImages(toListMap(src.getRoomImages()))
            .roomFacilities(toListMap(src.getRoomFacilities()))
            .roomSize(src.getRoomSize())

            // ===== PROPERTY DETAILS =====
            .propertySummary(src.getPropertySummary())
            .propertyImages(toListMap(src.getPropertyImages()))
            .propertyFacilities(toListMap(src.getPropertyFacilities()))

            // ===== FLAGS =====
            .refundable(Boolean.TRUE.equals(src.getRefundable()))
            .isRefundable(Boolean.TRUE.equals(src.getIsRefundable()))
            .wifiIncluded(src.getWifiIncluded())
            .breakfastIncluded(src.getBreakfastIncluded())
            .smokingAllowed(src.getSmokingAllowed())
            .build();
    }

    private Map<String, Object> toMap(Object obj) {
        return obj == null ? null : mapper.convertValue(obj, Map.class);
    }

    private List<Map<String, Object>> toListMap(List<?> list) {
        if (list == null) {
            return Collections.emptyList();
        }

        return list.stream()
            .map(item -> mapper.convertValue(
                item,
                new com.fasterxml.jackson.core.type.TypeReference<Map<String, Object>>() {}
            ))
            .collect(Collectors.toList());
    }

}
