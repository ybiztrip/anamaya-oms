package ai.anamaya.service.oms.core.enums;

public enum BookingTrainStatus {
    CREATED,
    RESERVED,
    BOOKED,
    APPROVED,
    REJECTED,
    ISSUING,
    ISSUED,
    CANCELLED;

    // ponytail: Postman only shows "Reserved" / "Ticketed"; cancel strings are a guess until Biztrip confirms the full list
    public static BookingTrainStatus fromPartnerStatus(String status, BookingTrainStatus current) {
        if (status == null) {
            return current;
        }

        return switch (status.trim().toUpperCase()) {
            case "TICKETED", "ISSUED" -> ISSUED;
            case "CANCELLED", "CANCELED", "CANCEL", "EXPIRED", "FAILED" -> CANCELLED;
            default -> current;
        };
    }
}
