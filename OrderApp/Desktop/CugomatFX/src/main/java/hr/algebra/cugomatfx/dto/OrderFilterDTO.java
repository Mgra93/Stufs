package hr.algebra.cugomatfx.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class OrderFilterDTO {
    private String companyCode;
    private LocalDate date;
    private String worker;
    private Integer status;
    private String user;
}
