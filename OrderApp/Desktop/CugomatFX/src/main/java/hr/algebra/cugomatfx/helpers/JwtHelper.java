package hr.algebra.cugomatfx.helpers;

import hr.algebra.cugomatfx.models.AccessData;
import hr.algebra.cugomatfx.models.JwtResponse;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;

import java.security.Key;

public class JwtHelper {
    private static final String SECRET = AppConfig.get("jwt.key");

    public static AccessData extractAccessData(JwtResponse jwtResponse) {
        String accessToken = jwtResponse.getAccessToken();
        String refreshToken = jwtResponse.getRefreshToken();

        String user = null;
        String role = null;

        if (accessToken != null && !accessToken.isEmpty()) {
            try {
                byte[] keyBytes = Decoders.BASE64.decode(SECRET);
                Key key = Keys.hmacShaKeyFor(keyBytes);

                Claims claims = Jwts.parserBuilder()
                        .setSigningKey(key)
                        .build()
                        .parseClaimsJws(accessToken)
                        .getBody();

                user = claims.getSubject();
                role = claims.get("role", String.class);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        return new AccessData(accessToken, refreshToken, user, role);
    }
}
