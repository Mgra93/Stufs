package hr.algebra.cugomatfx.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ClientUpdateDTO {
    private String clientCode;
    private Boolean vipDayActive;
    private Integer vipDayCode;
    private String locationSecret;
}
