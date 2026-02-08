package hr.algebra.cugomatfx.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class OrderDTO {
    private Integer id;
    private LocalDateTime createdOn;
    private BigDecimal totalPrice;
    private Boolean hasDiscount;
    private BigDecimal finalPrice;
    private Integer status;
    private UserPreviewDTO user;
    private UserPreviewDTO worker;
    private ClientPreviewDTO client;
    private String tableCode;
    private List<ProductDTO> products;
}
