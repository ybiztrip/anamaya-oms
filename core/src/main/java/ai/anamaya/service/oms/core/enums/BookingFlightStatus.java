package ai.anamaya.service.oms.core.enums;

public enum BookingFlightStatus {

    DRAFT,
    CREATED,
    BOOKED,
    ISSUING,
    ISSUED,
    ISSUANCE_FAILED;

    public static BookingFlightStatus fromPartnerStatus(String status) {
        if (status == null) {
            return DRAFT;
        }

        return switch (status.toUpperCase()) {
            case "ON_PROCESS_BOOKING" -> CREATED;
            case "OK", "BOOK", "BOOKED_DETAIL_CHANGED" -> BOOKED;
            case "ISSUING" -> ISSUING;
            case "ISSUED" -> ISSUED;
            case "ISSUANCE_FAILED" -> ISSUANCE_FAILED;
            default -> DRAFT;
        };
    }

    public static Boolean isSuccessBook(String status) {
        if (status == null) {
            return false;
        }

        return switch (status.toUpperCase()) {
            case "BOOK", "BOOKED_DETAIL_CHANGED" -> true;
            default -> false;
        };
    }
}