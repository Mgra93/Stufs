package hr.algebra.cugomatfx.enums;

public enum Language {
    HR("hr"),
    EN("en");

    private final String code;

    Language(String code) {
        this.code = code;
    }

    public String getCode() {
        return code;
    }
}
