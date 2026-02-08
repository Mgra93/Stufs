package hr.algebra.cugomatfx.models;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.time.format.DateTimeFormatter;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Order {
    private Integer id;
    private LocalDateTime createdOn;
    private BigDecimal totalPrice;
    private Boolean hasDiscount;
    private BigDecimal finalPrice;
    private Integer status;
    private String tableCode;
    private Client client;
    private User user;
    private User worker;
    private List<Product> products;

    @Override
    public String toString() {
        return tableCode + " " + createdOn.toLocalTime().format(DateTimeFormatter.ofPattern("HH:mm:ss"));
    }

    public int getTotalQuantity() {
        if (products == null || products.isEmpty()) {
            return 0;
        }

        return products.stream()
                .mapToInt(p -> p.getQuantity() != null ? p.getQuantity() : 0)
                .sum();
    }
}
