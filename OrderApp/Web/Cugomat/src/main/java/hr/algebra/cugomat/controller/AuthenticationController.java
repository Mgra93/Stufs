package hr.algebra.cugomat.controller;

import hr.algebra.cugomat.dto.AuthRequestDTO;
import hr.algebra.cugomat.dto.JwtResponseDTO;
import hr.algebra.cugomat.dto.RefreshTokenRequestDTO;
import hr.algebra.cugomat.models.RefreshToken;
import hr.algebra.cugomat.service.ApiService;
import hr.algebra.cugomat.service.JwtService;
import hr.algebra.cugomat.service.RefreshTokenService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.web.bind.annotation.*;

import java.util.Date;

@Slf4j
@RestController
@RequestMapping("/api/auth")
@AllArgsConstructor
public class AuthenticationController {
    private AuthenticationManager authenticationManager;
    private RefreshTokenService refreshTokenService;
    private JwtService jwtService;
    private ApiService apiService;

    @PostMapping("/login")
    public JwtResponseDTO authenticateAndGetToken(@RequestBody AuthRequestDTO authRequestDTO, HttpServletRequest request) {
        try {
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(authRequestDTO.getUsername(), authRequestDTO.getPassword()));

            if (authentication.isAuthenticated()) {
                String role = authentication.getAuthorities()
                        .stream()
                        .map(GrantedAuthority::getAuthority)
                        .findFirst()
                        .orElse(null);
                log.info("Login success");
                RefreshToken refreshToken = refreshTokenService.createRefreshToken(authRequestDTO.getUsername());
                apiService.saveLogin(authRequestDTO.getUsername(), request.getRemoteAddr());
                return JwtResponseDTO.builder()
                        .accessToken(jwtService.generateToken(authRequestDTO.getUsername(), role))
                        .refreshToken(refreshToken.getToken())
                        .build();
            } else {
                return JwtResponseDTO.builder()
                        .errorMessage("Invalid credentials")
                        .build();
            }
        } catch (Exception e) {
            log.error("Login error: {}", e.getMessage());
            return JwtResponseDTO.builder()
                    .accessToken(null)
                    .refreshToken(null)
                    .errorMessage("Invalid username or password")
                    .build();
        }
    }

    @PostMapping("/refreshToken")
    public JwtResponseDTO refreshToken(@RequestBody RefreshTokenRequestDTO refreshTokenRequestDTO) {
        if (refreshTokenRequestDTO != null) {
            return refreshTokenService.findByToken(refreshTokenRequestDTO.getToken())
                    .map(refreshTokenService::verifyExpiration)
                    .map(RefreshToken::getUser)
                    .map(userInfo -> {

                        String role = userInfo.getRole().getName();

                        String accessToken = jwtService.generateToken(userInfo.getUsername(), role);
                        return JwtResponseDTO.builder()
                                .accessToken(accessToken)
                                .refreshToken(refreshTokenRequestDTO.getToken())
                                .build();
                    })
                    .orElseThrow(() -> new RuntimeException("Refresh Token is not in DB..!!"));
        } else {
            return JwtResponseDTO.builder()
                    .accessToken(null)
                    .refreshToken(null)
                    .errorMessage("Bad request")
                    .build();
        }

    }

    @GetMapping("/token/expiry")
    public ResponseEntity<String> getTokenExpiry(@RequestParam String token) {
        try {
            Date expiryDate = jwtService.extractExpiration(token);
            return ResponseEntity.ok("Token expires at: " + expiryDate.toString());
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Invalid token: " + e.getMessage());
        }
    }

    @GetMapping("/ping")
    public ResponseEntity<String> ping() {
        return ResponseEntity.ok("Server is up");
    }
}
