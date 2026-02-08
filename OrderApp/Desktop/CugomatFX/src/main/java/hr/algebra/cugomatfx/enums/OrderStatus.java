package hr.algebra.cugomatfx.enums;

import hr.algebra.cugomatfx.helpers.MessageHelper;

import java.util.MissingResourceException;

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

    public static OrderStatus fromCode(int code) {
        for (OrderStatus s : values()) {
            if (s.code == code) return s;
        }
        return null;
    }

    @Override
    public String toString() {
        try {
            String key = "order.status." + code;
            return MessageHelper.getString(key);
        } catch (MissingResourceException e) {
            return "Missing key";
        }
    }
}
