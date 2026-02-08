package hr.algebra.cugomatfx.models;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AccessData {
    private String accessToken;
    private String refreshToken;
    private String user;
    private String role;

    public String getBearerToken() {
        if (accessToken != null && !accessToken.isEmpty()) {
            return "Bearer " + accessToken;
        }
        return null;
    }
}
