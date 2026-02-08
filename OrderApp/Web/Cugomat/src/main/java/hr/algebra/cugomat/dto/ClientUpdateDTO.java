package hr.algebra.cugomat.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ClientUpdateDTO {
    private String clientCode;
    private Boolean vipDayActive;
    private Integer vipDayCode;
    private String locationSecret;
}
