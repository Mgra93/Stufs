package hr.algebra.cugomat.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class WorkerDTO {
    private Integer id;
    private UserDTO user;
    private ClientDTO client;
}
