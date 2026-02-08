package hr.algebra.cugomat.models;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.Instant;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class RefreshToken {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "Id")
    private int id;
    @Column(name = "Token", nullable = false, length = 256)
    private String token;
    @Column(name = "ExpDate", nullable = false)
    private Instant expDate;
    @ManyToOne
    @JoinColumn(name = "UserId", referencedColumnName = "id")
    private User user;
}
