package ai.anamaya.service.oms.core.enums;

public enum BalanceSourceType {
    BOOKING((short) 1),
    PROCUREMENT((short) 2),
    ADJUSTMENT((short) 3);

    private final short value;
    BalanceSourceType(short value) { this.value = value; }
    public short getValue() { return value; }
}
