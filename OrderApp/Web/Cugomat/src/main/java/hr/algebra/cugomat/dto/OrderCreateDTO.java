package hr.algebra.cugomat.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class OrderCreateDTO {
    private String user;
    private String clientCode;
    private String tableCode;
    private List<ProductDTO> productList;
    private BigDecimal totalPrice;
    private Boolean hasDiscount;
    private BigDecimal finalPrice;
}
