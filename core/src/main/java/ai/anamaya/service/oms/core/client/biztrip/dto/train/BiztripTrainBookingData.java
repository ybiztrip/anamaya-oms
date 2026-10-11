package ai.anamaya.service.oms.core.client.biztrip.dto.train;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

// /train/book answers camelCase, /train/booking/detail answers PascalCase: read both case-insensitively
@Data
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonFormat(with = JsonFormat.Feature.ACCEPT_CASE_INSENSITIVE_PROPERTIES)
public class BiztripTrainBookingData {
    private String id;
    private String referenceCode;
    private String bookingCode;
    private String timeLimit;
    private String ticketed;
    private String status;
    private String errorMessage;
    private List<Passenger> passengers;
    private List<Payment> payments;

    public BigDecimal totalPaid() {
        if (payments == null) {
            return null;
        }
        return payments.stream()
            .filter(p -> "TOTAL_PAID".equalsIgnoreCase(p.getCode()))
            .map(Payment::getAmount)
            .findFirst()
            .orElse(null);
    }

    @Data
    @JsonIgnoreProperties(ignoreUnknown = true)
    @JsonFormat(with = JsonFormat.Feature.ACCEPT_CASE_INSENSITIVE_PROPERTIES)
    public static class Passenger {
        private Integer index;
        private String firstName;
        private String lastName;
        // "EKS-1/1C" = coach/seat
        private String seatNumber;
    }

    @Data
    @JsonIgnoreProperties(ignoreUnknown = true)
    @JsonFormat(with = JsonFormat.Feature.ACCEPT_CASE_INSENSITIVE_PROPERTIES)
    public static class Payment {
        private String code;
        private BigDecimal amount;
    }
}
