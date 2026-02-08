package hr.algebra.cugomat.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ChangeOrderStatusDTO {
    private int orderId;
    private int status;
    private String workerUsername;
}
