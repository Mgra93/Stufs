package hr.algebra.cugomat.enums;

public enum UserRole {
    ADMIN("ROLE_ADMIN"),
    WORKER("ROLE_WORKER"),
    USER("ROLE_USER");

    private final String code;

    UserRole(String code) {
        this.code = code;
    }

    public String getCode() {
        return code;
    }
}

