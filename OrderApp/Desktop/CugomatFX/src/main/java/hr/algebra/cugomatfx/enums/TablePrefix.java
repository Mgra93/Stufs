package hr.algebra.cugomatfx.enums;

public enum TablePrefix {
    INSIDE("I"),
    OUTSIDE("O"),
    BAR_ONE("BO"),
    BAR_TWO("BT");

    private final String code;

    TablePrefix(String code) {
        this.code = code;
    }

    public String getCode() {
        return code;
    }
}
