package hr.algebra.cugomatfx.mapper;

import hr.algebra.cugomatfx.dto.JwtResponseDTO;
import hr.algebra.cugomatfx.models.JwtResponse;

public class JWTMapper {
    public static JwtResponse toEntity(JwtResponseDTO dto) {
        if (dto == null) return null;

        JwtResponse user = new JwtResponse();
        user.setAccessToken(dto.getAccessToken());
        user.setRefreshToken(dto.getRefreshToken());
        user.setErrorMessage(dto.getErrorMessage());

        return user;
    }
}
