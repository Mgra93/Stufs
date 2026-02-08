package hr.algebra.cugomat.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class OrderFilterDTO {
    private String companyCode;
    private LocalDate date;
    private String worker;
    private Integer status;
    private String user;
}
