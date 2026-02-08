package hr.algebra.cugomat.dto;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ProductCreateDTO {
    private String name;
    private BigDecimal price;
    private Integer categoryId;
    private String clientCode;
}
