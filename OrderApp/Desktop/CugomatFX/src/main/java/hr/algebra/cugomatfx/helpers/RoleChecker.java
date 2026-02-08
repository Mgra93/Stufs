package hr.algebra.cugomatfx.helpers;

import com.fasterxml.jackson.databind.ObjectMapper;
import hr.algebra.cugomatfx.enums.UserRole;

import java.util.Base64;
import java.util.Map;

public class RoleChecker {
    private final String accessToken;
    private final String roleTag = "role";

    public RoleChecker(String accessToken) {
        this.accessToken = accessToken;
    }

    public UserRole getRole() {
        if (accessToken == null || accessToken.isEmpty()) return null;
        try {
            String[] parts = accessToken.split("\\.");
            if (parts.length < 2) return null;

            String payloadJson = new String(Base64.getUrlDecoder().decode(parts[1]));
            ObjectMapper mapper = new ObjectMapper();
            Map<String, Object> payload = mapper.readValue(payloadJson, Map.class);

            String roleStr = (String) payload.get(roleTag);
            if (roleStr == null) return null;

            return UserRole.valueOf(roleStr);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public boolean hasRole(UserRole expectedRole) {
        UserRole role = getRole();
        return expectedRole != null && expectedRole.equals(role);
    }

    public boolean isAdmin() {
        return hasRole(UserRole.ROLE_ADMIN);
    }

    public boolean isWorker() {
        return hasRole(UserRole.ROLE_WORKER);
    }

    public boolean isUser() {
        return hasRole(UserRole.ROLE_USER);
    }
}
