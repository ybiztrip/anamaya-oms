package ai.anamaya.service.oms.core.dto.response;

import ai.anamaya.service.oms.core.enums.BookingPaymentMethod;
import ai.anamaya.service.oms.core.enums.BookingTrainStatus;
import com.fasterxml.jackson.databind.JsonNode;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BookingTrainResponse {
    private Long id;
    private Long companyId;
    private Long bookingId;
    private String bookingCode;
    private String carrierNumber;
    private String trainName;
    private String subClass;
    private String seatClass;
    private String origin;
    private String destination;
    private LocalDateTime departureDatetime;
    private LocalDateTime arrivalDatetime;
    private BigDecimal totalAmount;
    private BigDecimal managementFeeAmount;
    private BookingPaymentMethod paymentMethod;
    private String otaReference;
    private LocalDateTime timeLimit;
    private List<Map<String, Object>> seats;
    private BookingTrainStatus status;
    private JsonNode metadata;
    private String errorMessage;
    private String approvedByName;
    private LocalDateTime approvedAt;
    private String rejectedByName;
    private LocalDateTime rejectedAt;
    private LocalDateTime createdAt;

    private List<BookingPaxResponse> paxs;
}
