package ai.anamaya.service.oms.core.enums;

import lombok.Getter;

@Getter
public enum BalanceTransactionType {
    CREDIT((short) 1),
    DEBIT((short) 2);

    private final short value;

    BalanceTransactionType(short value) {
        this.value = value;
    }

    public static BalanceTransactionType fromValue(short value) {
        for (BalanceTransactionType type : values()) {
            if (type.value == value) {
                return type;
            }
        }
        throw new IllegalArgumentException("Invalid BalanceTransactionType: " + value);
    }
}
