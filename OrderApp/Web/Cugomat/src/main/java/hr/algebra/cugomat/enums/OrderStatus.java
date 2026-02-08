package hr.algebra.cugomat.enums;

public enum OrderStatus {
    CREATED(0),
    RECEIVED(1),
    COMPLETED(2),
    REJECTED(3);

    private final int code;

    OrderStatus(int code) {
        this.code = code;
    }

    public int getCode() {
        return code;
    }
}
