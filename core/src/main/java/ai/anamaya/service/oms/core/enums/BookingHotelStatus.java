package ai.anamaya.service.oms.core.enums;

public enum BookingHotelStatus {
    CREATED,
    REJECTED,
    APPROVED,
    BOOKED,
    WAITING_PAYMENT,
    ISSUED,
    CANCELLED;

    public static BookingHotelStatus fromBookingPartnerStatus(String status) {
        if (status == null) {
            return CREATED;
        }

        return switch (status.toUpperCase()) {
            case "ISSUED" -> ISSUED;
            case "ON_PROCESS_BOOKING" -> WAITING_PAYMENT;
            case "CANCELLED" -> CANCELLED;
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

}
