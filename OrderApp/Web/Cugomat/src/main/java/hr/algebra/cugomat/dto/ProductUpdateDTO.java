package hr.algebra.cugomat.dto;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ProductUpdateDTO {
    private Integer id;
    private String name;
    private BigDecimal price;
    private Boolean active;
    private Integer categoryId;
}
