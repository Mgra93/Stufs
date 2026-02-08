package hr.algebra.cugomatfx.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class WorkerUpdateDTO {
    private String username;
    private String firstName;
    private String lastName;
    private String email;
    private String phone;
    private Boolean changePassword;
    private String password;
}
