package ai.anamaya.service.oms.core.enums;

public enum BookingHotelStatus {
    CREATED,
    REJECTED,
    APPROVED,
    BOOKED,
    WAITING_PAYMENT,
    PAID,
    ISSUING,
    ISSUED,
    CANCELLED;

    public static BookingHotelStatus fromBookingPartnerStatus(String status) {
        if (status == null) {
            return CREATED;
        }

        return switch (status.toUpperCase()) {
            case "ON_PROCESS_BOOKING" -> WAITING_PAYMENT;
            case "PAID" -> PAID;
            case "WAITING_FOR_ISSUANCE", "ISSUING" -> ISSUING;
            case "ISSUED" -> ISSUED;
            case "CANCELLED", "BOOKING_EXPIRED", "PAYMENT_EXPIRED", "FAILED" -> CANCELLED;
            default -> CREATED;
        };
    }

    public static BookingHotelStatus fromString(String value) {
        if (value == null || value.isBlank()) return null;

        try {
            return BookingHotelStatus.valueOf(value.trim().toUpperCase());
        } catch (IllegalArgumentException e) {
            return null;
        }
    }

    public static boolean isValidToUpdate(BookingHotelStatus newStatus, BookingHotelStatus oldStatus) {
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
