package hr.algebra.cugomatfx.enums;

public enum UserRole {
    ROLE_ADMIN,
    ROLE_WORKER,
    ROLE_USER;

    public static UserRole fromString(String roleName) {
        try {
            return UserRole.valueOf(roleName);
        } catch (Exception e) {
            return null;
        }
    }
}
