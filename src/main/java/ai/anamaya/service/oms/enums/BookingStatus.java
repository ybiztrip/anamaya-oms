package ai.anamaya.service.oms.enums;

public enum BookingStatus {
    CREATED((short) 1),
    CONFIRMED((short) 2),
    CANCELLED((short) 3);

    private final short value;

    BookingStatus(short value) {
        this.value = value;
    }

    public short getValue() {
        return value;
    }
}
