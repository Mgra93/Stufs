package hr.algebra.cugomatfx.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class WorkerDTO {
    private Integer id;
    private UserDTO user;
    private ClientDTO client;
}
