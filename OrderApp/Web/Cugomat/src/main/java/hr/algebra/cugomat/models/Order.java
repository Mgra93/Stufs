package hr.algebra.cugomat.models;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "Orders")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Order implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "Id")
    private Integer id;
    @Column(name = "CreatedOn", nullable = false)
    private LocalDateTime createdOn;
    @Column(name = "TotalPrice", nullable = false, precision = 10, scale = 2)
    private BigDecimal totalPrice;
    @Column(name = "HasDiscount", nullable = false)
    private Boolean hasDiscount;
    @Column(name = "FinalPrice", nullable = false, precision = 10, scale = 2)
    private BigDecimal finalPrice;
    @Column(name = "Status", nullable = false)
    private Integer status;
    @Column(name = "TableCode", nullable = false)
    private String tableCode;
    @ManyToOne
    @JoinColumn(name = "ClientId", nullable = false)
    private Client client;
    @ManyToOne
    @JoinColumn(name = "UserId", nullable = false)
    private User user;
    @ManyToOne
    @JoinColumn(name = "WorkerId")
    private User worker;
    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<OrderProduct> orderProducts;
}
