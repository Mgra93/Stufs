package hr.algebra.cugomatfx.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserPreviewDTO {
    private String username;
    private String firstName;
    private String lastName;
    private String email;
    private String phone;
}
