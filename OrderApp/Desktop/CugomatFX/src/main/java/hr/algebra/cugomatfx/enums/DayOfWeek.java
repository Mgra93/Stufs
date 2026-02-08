package hr.algebra.cugomatfx.enums;

import hr.algebra.cugomatfx.helpers.MessageHelper;

import java.util.MissingResourceException;

public enum DayOfWeek {
    MONDAY(1),
    TUESDAY(2),
    WEDNESDAY(3),
    THURSDAY(4),
    FRIDAY(5),
    SATURDAY(6),
    SUNDAY(7);

    private final int code;

    DayOfWeek(int code) {
        this.code = code;
    }

    public int getCode() {
        return code;
    }

    public static DayOfWeek fromCode(int code) {
        for (DayOfWeek s : values()) {
            if (s.code == code) return s;
        }
        return null;
    }

    @Override
    public String toString() {
        try {
            String key = "day.in.week." + code;
            return MessageHelper.getString(key);
        } catch (MissingResourceException e) {
            return "Missing key";
        }
    }
}
