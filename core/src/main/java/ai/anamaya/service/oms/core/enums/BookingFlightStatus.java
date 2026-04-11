package ai.anamaya.service.oms.core.enums;

public enum BookingFlightStatus {
    CREATED,
    BOOKED,
    APPROVED,
    REJECTED,
    CANCELLED,
    WAITING_PAYMENT,
    PAID,
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
            case "PAID" -> PAID;
            case "WAITING_FOR_ISSUANCE", "ISSUING" -> ISSUING;
            case "ISSUED" -> ISSUED;
            case "ISSUANCE_FAILED" -> ISSUANCE_FAILED;
            case "BOOKING_EXPIRED", "PAYMENT_EXPIRED", "FAILED" -> CANCELLED;
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

    public static BookingFlightStatus fromString(String value) {
        if (value == null || value.isBlank()) return null;

        try {
            return BookingFlightStatus.valueOf(value.trim().toUpperCase());
        } catch (IllegalArgumentException e) {
            return null;
        }
    }

    public static boolean isValidToUpdate(BookingFlightStatus newStatus, BookingFlightStatus oldStatus) {
        return switch (newStatus) {
            case CANCELLED -> true;
            case PAID ->
                oldStatus != CANCELLED &&
                    oldStatus != ISSUING &&
                    oldStatus != ISSUED;
            case ISSUING ->
                oldStatus != CANCELLED &&
                    oldStatus != ISSUED;
            case ISSUED ->
                oldStatus != CANCELLED;
            case BOOKED ->
                oldStatus == CREATED ||
                    oldStatus == APPROVED;
            default -> false;
        };
    }

}