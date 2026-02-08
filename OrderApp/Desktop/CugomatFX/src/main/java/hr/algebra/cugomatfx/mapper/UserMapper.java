package hr.algebra.cugomatfx.mapper;

import hr.algebra.cugomatfx.dto.UserDTO;
import hr.algebra.cugomatfx.dto.UserPreviewDTO;
import hr.algebra.cugomatfx.models.User;

public class UserMapper {
    public static User toEntity(UserDTO dto) {
        if (dto == null) return null;

        User user = new User();
        user.setUsername(dto.getUsername());
        user.setPassword(dto.getPassword());
        user.setFirstName(dto.getFirstName());
        user.setLastName(dto.getLastName());
        user.setEmail(dto.getEmail());
        user.setPhone(dto.getPhone());
        return user;
    }

    public static User toEntity(UserPreviewDTO dto) {
        if (dto == null) return null;

        User user = new User();
        user.setUsername(dto.getUsername());
        user.setFirstName(dto.getFirstName());
        user.setLastName(dto.getLastName());
        user.setEmail(dto.getEmail());
        user.setPhone(dto.getPhone());
        return user;
    }

    public static UserDTO toDTO(User object) {
        if (object == null) return null;

        UserDTO dto = new UserDTO();
        dto.setUsername(object.getUsername());
        dto.setPassword(object.getPassword());
        dto.setFirstName(object.getFirstName());
        dto.setLastName(object.getLastName());
        dto.setEmail(object.getEmail());
        dto.setPhone(object.getPhone());
        return dto;
    }
}
