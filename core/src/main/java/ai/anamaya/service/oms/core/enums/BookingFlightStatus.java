package ai.anamaya.service.oms.core.enums;

public enum BookingFlightStatus {
    CREATED,
    BOOKED,
    APPROVED,
    CANCELLED,
    ISSUING,
    ISSUED,
    ISSUANCE_FAILED;

    public static BookingFlightStatus fromBookingPartnerStatus(String status) {
        if (status == null) {
            return CANCELLED;
        }

        return switch (status.toUpperCase()) {
            case "ON_PROCESS_BOOKING" -> CREATED;
            case "OK", "BOOK", "BOOKED", "BOOKED_DETAIL_CHANGED" -> BOOKED;
            case "ISSUING" -> ISSUING;
            case "ISSUED" -> ISSUED;
            case "ISSUANCE_FAILED" -> ISSUANCE_FAILED;
            default -> CREATED;
        };
    }

    public static BookingFlightStatus fromPaymentPartnerStatus(String status) {
        if (status == null) {
            return CREATED;
        }

        return switch (status.toUpperCase()) {
            case "OK" -> ISSUED;
            default -> CREATED;
        };
    }

    public static Boolean isSuccessBook(String status) {
        if (status == null) {
            return false;
        }

        return switch (status.toUpperCase()) {
            case "OK", "BOOK", "BOOKED", "BOOKED_DETAIL_CHANGED" -> true;
            default -> false;
        };
    }
}