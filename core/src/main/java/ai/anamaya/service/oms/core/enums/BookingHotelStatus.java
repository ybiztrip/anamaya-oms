package ai.anamaya.service.oms.core.enums;

public enum BookingHotelStatus {
    DRAFT,
    CREATED,
    BOOKED,
    ISSUED,
    CANCELLED;

    public static BookingHotelStatus fromBookingPartnerStatus(String status) {
        if (status == null) {
            return DRAFT;
        }

        return switch (status.toUpperCase()) {
            case "ISSUED" -> ISSUED;
            case "CANCELLED" -> CANCELLED;
            default -> CREATED;
        };
    }
}
